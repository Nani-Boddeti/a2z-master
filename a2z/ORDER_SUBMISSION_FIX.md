# Order Submission Duplicate Entry Fix

## Problem
**Error:** 
```
org.springframework.dao.DataIntegrityViolationException: could not execute statement 
[Duplicate entry '3559' for key 'order_entry.UK_rq6d7vtm4jcog1ed388arp072']
```

## Root Cause
The `DefaultOrderService` was saving OrderEntry entities **twice**:

1. **First save** in `saveEntries()` method:
   - Called `orderEntryRepo.save(orderEntry)` 
   - Also called `adPostRepo.save(adPost)`

2. **Second save** in `submitOrderInternal()` method:
   - Called `orderEntryRepo.save(entry)` again
   - Called `adPostRepository.save(adPost)` again

This double-save violated the unique constraint on the `order_entry` table, causing a duplicate entry error.

## Solution
Modified the code to ensure entries and AdPost are saved **only once** in the proper order:

### 1. **Updated `saveEntries()` method:**
```java
// REMOVED: adPostRepo.save(adPost);
// REMOVED: orderEntryRepo.save(orderEntry);

// ADDED COMMENT: "Do NOT save here as this will be handled in submitOrderInternal()"
```
- The method now only **creates** OrderEntry objects and associates them with AdPost
- Does **NOT** persist them to the database
- This prepares the entries for a single save operation later

### 2. **Optimized `submitOrderInternal()` method:**
```java
// Single save operation for each entry
entry.setOrder(order);
orderEntryRepo.save(entry);  // Save ONCE

// Save AdPost associated with the entry
adPost.setActive(false);
adPost.setModifiedTime(new Date());
adPostRepository.save(adPost);  // Save ONCE
```

## Benefits
✅ **Prevents duplicate entry errors** - Each OrderEntry is saved only once  
✅ **Maintains data integrity** - Unique constraints are respected  
✅ **Clear separation of concerns** - `saveEntries()` prepares, `submitOrderInternal()` persists  
✅ **Better transaction handling** - All saves happen in a single logical flow  
✅ **Improved code readability** - Comments clarify the save strategy  

## Execution Flow
1. User calls `submitOrder(order, isExtended, originalOrder)`
2. Order and entries are prepared by calling `saveEntries()` (no DB saves)
3. `submitOrderInternal()` is called, which:
   - Saves the Order
   - Saves each OrderEntry (once) and updates associated AdPost
   - Creates and saves ApprovalRequest
   - Updates Order with ApprovalRequest reference
4. All operations complete successfully without duplicate entry violations

## Files Modified
- `/Users/nani/DriveD/Projects/Projects/a2z/src/main/java/com/a2z/services/impl/DefaultOrderService.java`

## Testing Recommendation
1. Test order submission with single OrderEntry
2. Test order submission with multiple OrderEntries
3. Verify that no duplicate entries are created
4. Verify that all AdPosts are marked as inactive
5. Verify that ApprovalRequest is created and linked to the Order

