# Entity Cascade & Security Review: dao Directory

## General Observations
- CascadeType.ALL is widely used in @OneToOne and @ManyToOne relationships.
- No orphanRemoval detected in @OneToMany or @ManyToMany relationships.
- Sensitive data (e.g., password in Customer) present.
- Eager fetching is used in some relationships.

## Risks & Recommendations

### CascadeType.ALL
- **Risk**: CascadeType.ALL on @ManyToOne and @OneToOne can cause unintended deletions/updates. Deleting a parent may delete children or related entities unexpectedly.
- **Recommendation**: Use cascade selectively (e.g., CascadeType.PERSIST, CascadeType.MERGE) unless you are certain all operations should cascade. Avoid CascadeType.ALL on @ManyToOne unless business logic requires it.

### Orphan Removal
- **Risk**: Orphaned records may remain if orphanRemoval is not set on @OneToMany/@ManyToMany.
- **Recommendation**: Use orphanRemoval=true where appropriate to ensure data integrity.

### Sensitive Data
- **Risk**: Password field in Customer entity is a security risk if not hashed or protected.
- **Recommendation**: Always hash passwords before storing. Do not expose password fields in DTOs, APIs, or serialization. Consider using @JsonIgnore or similar.

### Fetch Type
- **Risk**: EAGER fetching can lead to performance issues and excessive data exposure.
- **Recommendation**: Prefer LAZY fetching for collections and large relationships unless EAGER is required for business logic.

### Access Control
- **Risk**: Entities do not enforce access control; this should be handled at the service or API layer.
- **Recommendation**: Ensure service/API layers validate user permissions before accessing or modifying entity data.

## Entity-Specific Notes
- **OrderEntry**: CascadeType.ALL on multiple relationships. Review if all cascades are necessary. Consider LAZY fetch for large relationships.
- **Customer**: Password field present. Ensure hashing and protection. Multiple relationships with cascade; review necessity.
- **A2zOrder, PrimeUser, ApprovalRequest, AdPost, A2zAddress**: CascadeType.ALL used; review for necessity and possible data integrity issues.

## Best Practices
- Use cascade only where required by business logic.
- Hash and protect sensitive fields.
- Use LAZY fetch for large or sensitive relationships.
- Use orphanRemoval to prevent orphaned records.
- Enforce access control at service/API layer.

---
This review covers cascade and security measures for all entities under the dao directory. For detailed code changes, review each entity's relationships and sensitive fields as per recommendations above.

