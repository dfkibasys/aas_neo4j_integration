---
# Cypher Query Descriptions for Submodel Deletion/Cleanup

This document describes a series of Cypher queries designed to systematically delete or clean up a specific Submodel and its related elements and references within a Neo4j graph database, based on certain conditions (primarily, the absence of external references and `sourceUrl`).

The queries are intended to be executed sequentially.

---

## Queries

### 1. Delete Direct Submodel References

**Query:**
```cypher
MATCH (s:Submodel {id: $submodel.id})-[rel:HAS_REFERENCE|HAS_SEMANTIC]->(t)
WHERE t.sourceUrl IS NULL AND NOT EXISTS {
  (t)<-[]-(o:Identifiable|SubmodelElement)
  WHERE (o.id IS NULL OR o.id <> $submodel.id) AND (o.smId IS NULL OR o.smId <> $submodel.id)
}
DETACH DELETE t
```
**Description:** This query identifies and deletes **`t`** nodes (targets of `HAS_REFERENCE` or `HAS_SEMANTIC` relations) that are directly referenced by the specified **Submodel** (`s`). A `t` node is deleted only if it has no `sourceUrl` and is not referenced by any other `Identifiable` or `SubmodelElement` outside of the current Submodel's scope.

---

### 2. Delete Direct Submodel Reference Relations

**Query:**
```cypher
MATCH (s:Submodel {id: $submodel.id})-[rel:HAS_REFERENCE|HAS_SEMANTIC]->()
DELETE rel
```
**Description:** This query deletes all **`HAS_REFERENCE` and `HAS_SEMANTIC` relations** that originate directly from the specified **Submodel** (`s`). This cleans up relations to `t` nodes that either were deleted by the previous query or were not deleted because they had external references.

---

### 3. Delete Nested Submodel Element References

**Query:**
```cypher
MATCH (s:Submodel {id: $submodel.id})
  -[r:HAS_ELEMENT*0..]->(n:SubmodelElement)
  -[ref:HAS_REFERENCE|HAS_SEMANTIC]->(t)
WHERE t.sourceUrl IS NULL
AND NOT EXISTS {
  (t)<-[]-(o:Identifiable|SubmodelElement)
  WHERE (o.id IS NULL OR o.id <> s.id) AND (o.smId IS NULL OR o.smId <> s.id)
}
DETACH DELETE t
```
**Description:** This query focuses on **`t` nodes referenced by `SubmodelElement`s** that are part of the specified Submodel's hierarchy. It deletes these `t` nodes if they lack a `sourceUrl` and are not externally referenced by other `Identifiable` or `SubmodelElement`s. This is crucial for deep cleanup of the Submodel's internal references.

---

### 4. Delete Nested Submodel Element Reference Relations

**Query:**
```cypher
MATCH (s:Submodel {id: $submodel.id})-[r:HAS_ELEMENT*0..]->(n:SubmodelElement)
OPTIONAL MATCH (n)-[rel:HAS_REFERENCE|HAS_SEMANTIC]->(t)
DELETE rel
```
**Description:** This query removes **`HAS_REFERENCE` and `HAS_SEMANTIC` relations** that originate from `SubmodelElement`s (`n`) within the specified Submodel's hierarchy. It uses `OPTIONAL MATCH` to handle `SubmodelElement`s that might not have such relations. This cleans up the connections to `t` nodes that either were deleted by the previous query or were retained due to external references.

---

### 5. Remove Properties from Externally Referenced Submodel Elements

**Query:**
```cypher
MATCH p = ((s:Submodel {id: $submodel.id})-[r:HAS_ELEMENT*0..]->(n:SubmodelElement))
WHERE EXISTS {
  MATCH (o)-[incoming]->(n)
  WHERE (o.id IS NULL OR o.id <> $submodel.id) AND (o.smId IS NULL OR o.smId <> $submodel.id)
}
REMOVE n.value, n.sourceUrl
```
**Description:** This query targets `SubmodelElement`s (`n`) that are part of the specified Submodel but are also **externally referenced**. Since these `SubmodelElement`s cannot be deleted, this query acts as a cleanup mechanism, removing their `value` and `sourceUrl` properties to signify their "detached" status from the original Submodel's context.

---

### 6. Delete Non-Externally Referenced Submodel Elements

**Query:**
```cypher
MATCH p = ((s:Submodel {id: $submodel.id})-[r:HAS_ELEMENT*0..]->(n:SubmodelElement))
WHERE NOT EXISTS {
  MATCH (o:Identifiable|SubmodelElement)-[incoming]->(n)
  WHERE (o.id IS NULL OR o.id <> s.id) AND (o.smId IS NULL OR o.smId <> s.id)
}
WITH length(p) AS len, n
ORDER BY len DESC
DETACH DELETE n
```
**Description:** This query deletes **`SubmodelElement`s** (`n`) within the Submodel's hierarchy that are **not externally referenced**. The `ORDER BY len DESC` ensures that elements deeper in the hierarchy (longer paths from `s`) are deleted first, which is a robust strategy for hierarchical deletions using `DETACH DELETE`.

---

### 7. Remove Properties from Externally Referenced Submodel

**Query:**
```cypher
MATCH (s:Submodel {id: $submodel.id})
WHERE EXISTS {
  MATCH (o:Identifiable|SubmodelElement)-[incoming]->(s)
  WHERE (o.id IS NULL OR o.id <> $submodel.id) AND (o.smId IS NULL OR o.smId <> $submodel.id)
}
REMOVE s.sourceUrl, s.registrationTime
```
**Description:** This query targets the **Submodel node (`s`) itself**. If the Submodel is **externally referenced**, it cannot be fully deleted. Instead, this query removes its `sourceUrl` and `registrationTime` properties, signaling its altered status.

---

### 8. Delete Non-Externally Referenced Submodel

**Query:**
```cypher
MATCH (s:Submodel {id: $submodel.id})
WHERE NOT EXISTS {
  MATCH (o:Identifiable|SubmodelElement)-[incoming]->(s)
  WHERE (o.id IS NULL OR o.id <> $submodel.id) AND (o.smId IS NULL OR o.smId <> $submodel.id)
}
DETACH DELETE s
```
**Description:** This final query attempts to **delete the Submodel node (`s`)** itself. It only proceeds if the Submodel is **not externally referenced** by any `Identifiable` or `SubmodelElement`, ensuring that only "isolated" Submodels are removed.