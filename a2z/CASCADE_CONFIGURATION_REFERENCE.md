# Entity Cascade Configuration - Quick Reference

## Summary of Changes

### Critical Fix: Cascade Configuration

| Entity | Relationship | Before | After | Reason |
|--------|--------------|--------|-------|--------|
| OrderEntry | order (ManyToOne) | PERSIST, MERGE | REFRESH | Prevent dual cascade paths |
| AdPost | orderEntries (OneToMany) | ALL | NONE | Owning side is A2zOrder, not AdPost |
| A2zOrder | customer (ManyToOne) | PERSIST, MERGE | REFRESH | Reference entity, should exist independently |
| A2zOrder | price (ManyToOne) | PERSIST, MERGE | REFRESH | Reference entity, should exist independently |
| A2zOrder | originalVersion (OneToOne) | PERSIST, MERGE | REFRESH | Reference to existing order |
| PrimeUser | customer (ManyToOne) | PERSIST, MERGE | REFRESH | Reference entity |
| PrimeUser | price (ManyToOne) | PERSIST, MERGE | REFRESH | Reference entity |
| PrimeUser | primeGroup (ManyToOne) | PERSIST, MERGE | REFRESH | Reference entity |
| ApprovalRequest | customer (ManyToOne) | PERSIST, MERGE | REFRESH | Reference entity |
| ApprovalRequest | adPost (OneToOne) | PERSIST, MERGE | REFRESH | Reference to existing post |
| ApprovalRequest | order (OneToOne) | PERSIST, MERGE | REFRESH | Reference to existing order |
| A2zAddress | country (OneToOne) | PERSIST, MERGE | REFRESH | Reference entity |
| A2zAddress | customer (ManyToOne) | PERSIST, MERGE | REFRESH | Reference entity |

### Fetch Type Optimization

All reference relationships now use `fetch = FetchType.LAZY`:
- OrderEntry.order
- A2zOrder.customer
- A2zOrder.price
- A2zOrder.originalVersion
- PrimeUser.customer, price, primeGroup
- ApprovalRequest.customer
- A2zAddress.country, customer
- AdPost.orderEntries

### Cascade Configuration Principles Applied

**Rule 1: Owning Side Only**
- Cascade is only configured on the owning side of a bidirectional relationship
- Non-owning side (mappedBy) has no cascade or cascade = {}

**Rule 2: Reference vs. Owned**
```
Reference Entities (ManyToOne/OneToOne without owner):
  ↳ cascade = REFRESH only
  ↳ fetch = LAZY
  ↳ Should exist independently in database

Owned Entities (OneToMany/OneToOne with owner):
  ↳ cascade = PERSIST, MERGE (or ALL if orphanRemoval needed)
  ↳ fetch = LAZY (unless specific business need for EAGER)
  ↳ Lifecycle tied to parent entity
```

**Rule 3: No Cascading Up**
- ManyToOne relationships should NOT cascade PERSIST/MERGE
- These are navigating to parent/reference entities
- Parent entities have their own lifecycle

## Cascade Chain Resolution

### Before (Problem)
```
Order.save() 
  → cascades to order.entries
    → cascades to each OrderEntry
      → cascades to orderEntry.order (back to same Order!)
      → cascades to orderEntry.adPost
        → cascades to adPost.orderEntries (back to same OrderEntry!)
        → cascades to adPost.customer
          → cascades to customer.orders (more Orders!)
```
Result: **Multiple persistence paths → Duplicate attempts → Unique constraint violation**

### After (Solution)
```
Order.save()
  → cascades PERSIST/MERGE to order.entries (Owning side)
    → saves each OrderEntry
      → REFRESH only on orderEntry.order (no re-cascade)
      → REFRESH only on orderEntry.adPost (no re-cascade)
    → Updates adPost status (no cascade)
  → cascades PERSIST/MERGE to deliveryAddress
  → cascades PERSIST/MERGE to paymentInfo
```
Result: **Single cascade path → One persistence per entity → No constraint violations**

## Testing Checklist

- [ ] Order submission with single OrderEntry succeeds
- [ ] Order submission with multiple OrderEntries succeeds  
- [ ] No "Duplicate entry" errors
- [ ] OrderEntries are properly linked to Orders
- [ ] AdPost status is updated to inactive
- [ ] ApprovalRequest is created for the Order
- [ ] Fetching orders doesn't cause excessive queries
- [ ] Deleting an order deletes associated entries (orphanRemoval)
- [ ] Deleting a customer fails (orders exist - FK constraint)

## Performance Improvements

- **LAZY loading** on reference relationships reduces unnecessary data loading
- **Single cascade path** reduces Hibernate overhead
- **Fewer unnecessary updates** due to not cascading on references
- Expected improvement in order submission response time

## Backward Compatibility

✅ **No breaking changes to business logic**
✅ **No database schema changes required**
✅ **Only JPA annotation changes**
✅ Existing data remains intact
✅ Can be deployed without migration

## Related Documents

- `DATABASE_ENTITY_CASCADE_FIX.md` - Detailed explanation of cascade issue
- `ORDER_SUBMISSION_FIX.md` - Service layer fixes to prevent duplicate saves
- `DAO_ENTITY_REVIEW.md` - Initial entity security review

