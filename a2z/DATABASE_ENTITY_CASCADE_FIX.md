# Database Entity Cascade Configuration Fix

## Problem Identified
The **Duplicate Entry** error when saving orders was caused by **improper Hibernate cascade configurations** in the entity relationships, not just by the service layer logic.

**Error Message:**
```
org.springframework.dao.DataIntegrityViolationException: could not execute statement 
[Duplicate entry '3559' for key 'order_entry.UK_rq6d7vtm4jcog1ed388arp072']
```

## Root Cause Analysis

### The Cascade Chain Problem
The cascading was configured incorrectly on bidirectional and multi-path relationships:

1. **OrderEntry** had two parent relationships:
   - `@ManyToOne(cascade = PERSIST/MERGE)` to `A2zOrder`
   - `@ManyToOne(cascade = PERSIST/MERGE)` to `AdPost`

2. **AdPost** had:
   - `@OneToMany(cascade = CascadeType.ALL)` back to `OrderEntry`

3. **A2zOrder** had:
   - `@OneToMany(cascade = PERSIST/MERGE)` to `OrderEntry`

**Result:** When saving an Order, Hibernate would attempt to cascade save the OrderEntry:
- Via the Order → OrderEntry relationship (A2zOrder.entries)
- Via the AdPost → OrderEntry relationship (AdPost.orderEntries)
- Via reverse cascades

This created **multiple persistence paths** to the same entity, causing duplicate attempts to insert and violating unique constraints.

## Solutions Implemented

### 1. **Fixed AdPost.orderEntries Relationship**
```java
// BEFORE
@OneToMany(mappedBy = "adPost", cascade = CascadeType.ALL, orphanRemoval = true)
private List<OrderEntry> orderEntries;

// AFTER
@OneToMany(mappedBy = "adPost", cascade = {}, fetch = FetchType.LAZY)
@JsonIgnoreProperties("adPost")
private List<OrderEntry> orderEntries;
```
**Rationale:** Removed cascade from the non-owning side. OrderEntry is owned by A2zOrder, not AdPost. The cascade is handled on the owning side only.

### 2. **Changed ManyToOne Cascades to REFRESH Only**
Applied across multiple entities:
- **OrderEntry.order**: `cascade = REFRESH`
- **A2zOrder.customer & A2zOrder.price**: `cascade = REFRESH`
- **PrimeUser.customer, price, primeGroup**: `cascade = REFRESH`
- **ApprovalRequest.customer, adPost, order**: `cascade = REFRESH`
- **A2zAddress.customer, country**: `cascade = REFRESH`

```java
// BEFORE - ManyToOne with PERSIST/MERGE
@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
@JoinColumn(name = "order_id")
private A2zOrder order;

// AFTER - ManyToOne with REFRESH only
@ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.LAZY)
@JoinColumn(name = "order_id")
private A2zOrder order;
```

**Rationale:** 
- ManyToOne relationships reference entities that should **exist independently**
- Cascading PERSIST/MERGE on ManyToOne creates complex cascade chains
- REFRESH only is appropriate for updating entity state without cascading writes
- LAZY fetch improves performance by avoiding unnecessary loads

### 3. **Changed OneToOne Reference Cascades to REFRESH Only**
Applied to:
- **A2zOrder.originalVersion**: `cascade = REFRESH`
- **A2zAddress.country**: `cascade = REFRESH`
- **ApprovalRequest.adPost, order**: `cascade = REFRESH`

```java
// BEFORE - OneToOne with PERSIST/MERGE
@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
@JoinColumn(name = "a2zOrder_id")
private A2zOrder originalVersion;

// AFTER - OneToOne with REFRESH only
@OneToOne(cascade = {CascadeType.REFRESH})
@JoinColumn(name = "a2zOrder_id")
private A2zOrder originalVersion;
```

**Rationale:** Similar to ManyToOne - references to existing entities should not be cascaded for write operations.

### 4. **Preserved Cascade on Owned Entities**
Kept original cascades on entities that are **owned** by the parent:
- **A2zOrder.entries**: `cascade = PERSIST/MERGE` with `orphanRemoval = true`
- **A2zOrder.deliveryAddress**: `cascade = PERSIST/MERGE`
- **A2zOrder.paymentInfo**: `cascade = PERSIST/MERGE`

**Rationale:** These entities are owned/created as part of the Order and should be cascaded.

### 5. **Added LAZY Fetch Where Appropriate**
Added `fetch = FetchType.LAZY` to all reference relationships to:
- Improve query performance
- Prevent excessive data loading
- Allow on-demand loading when needed

## Entity Relationship Architecture After Fix

```
A2zOrder (Owning Side)
├── entries (OneToMany with cascade PERSIST/MERGE, orphanRemoval)
│   └── OrderEntry (Entity)
│       ├── order (ManyToOne, cascade REFRESH only) - References back to A2zOrder
│       └── adPost (ManyToOne, cascade REFRESH only) - Reference to existing AdPost
│
├── customer (ManyToOne, cascade REFRESH only) - Reference to existing Customer
├── price (ManyToOne, cascade REFRESH only) - Reference to existing Price
├── deliveryAddress (OneToOne, cascade PERSIST/MERGE) - Owned entity
└── paymentInfo (OneToOne, cascade PERSIST/MERGE) - Owned entity

AdPost (Non-Owning Side)
├── orderEntries (OneToMany, cascade = NONE) - No cascade, read-only access
└── customer (ManyToOne, cascade REFRESH only) - Reference to existing Customer
```

## Files Modified

1. **OrderEntry.java**
   - Changed order relationship cascade to REFRESH only
   - Added LAZY fetch

2. **A2zOrder.java**
   - Changed customer & price cascades to REFRESH
   - Changed originalVersion cascade to REFRESH
   - Added LAZY fetch to reference relationships

3. **AdPost.java**
   - Removed cascade from orderEntries (set to empty list)

4. **PrimeUser.java**
   - Changed all ManyToOne cascades to REFRESH
   - Added LAZY fetch to all relationships

5. **ApprovalRequest.java**
   - Changed all OneToOne and ManyToOne cascades to REFRESH
   - Added LAZY fetch

6. **A2zAddress.java**
   - Changed country cascade to REFRESH
   - Changed customer cascade to REFRESH
   - Added LAZY fetch

## Benefits

✅ **Eliminates duplicate entry errors** - Single cascade path per entity
✅ **Improves data integrity** - References to independent entities are not inadvertently modified
✅ **Better performance** - LAZY loading prevents unnecessary data fetching
✅ **Clearer intent** - Cascade configuration now reflects business logic
✅ **Prevents N+1 queries** - LAZY loading allows controlled fetching
✅ **Simplified transaction handling** - Fewer unintended cascade operations

## Testing Recommendations

1. **Test Order Submission:**
   - Submit order with single OrderEntry
   - Submit order with multiple OrderEntries
   - Verify no duplicate entry errors

2. **Verify Relationships:**
   - Verify OrderEntries are persisted correctly
   - Verify AdPost status is updated correctly
   - Verify ApprovalRequest is created

3. **Test Cascades:**
   - Update order and verify entries are updated
   - Remove order and verify entries are deleted (due to orphanRemoval)
   - Try deleting customer with active orders (should fail with FK constraint)

4. **Performance Test:**
   - Verify LAZY loading doesn't cause N+1 query issues in service layer

## Migration Notes

If you have existing data with orphaned OrderEntry records, you may need to:
1. Clean up orphaned records before deploying
2. Or temporarily enable cascading to clean up on first deployment
3. Verify database constraints allow the schema changes

No schema changes are required - only JPA annotations are modified.

