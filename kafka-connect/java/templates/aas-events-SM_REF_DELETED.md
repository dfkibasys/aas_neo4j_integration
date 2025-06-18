---

# Deleting a Specific Submodel Reference and Potentially the Submodel Node

This document describes the Cypher statement used to remove a specific `HAS_SUBMODEL` relationship from an Asset Administration Shell (AAS) to a particular Submodel. Additionally, it includes a conditional deletion of the Submodel node itself, if certain criteria are met.

---

## 1. Delete the HAS_SUBMODEL Relationship

**Objective:** To remove the direct `HAS_SUBMODEL` relationship between a specific AAS and a specific Submodel.

**Logic:**
* The statement first **matches** the `AssetAdministrationShell` node using its `id` (`$shell.id`).
* It then matches the `HAS_SUBMODEL` relationship (`r`) extending from this AAS to a `Submodel` node (`s`) with the specified `id` (`$submodel.id`).
* Finally, the matched relationship `r` is **deleted**. This breaks the direct link from the AAS to the Submodel.

```cypher
MATCH (a:AssetAdministrationShell { id: $shell.id })-[r:HAS_SUBMODEL]->(s:Submodel {id: $submodel.id})
DELETE r
```

---

## 2. Conditionally Delete the Submodel Node

**Objective:** To delete the Submodel node itself, but only if it's considered a "local placeholder" and is no longer referenced by any other nodes in the graph.

**Logic:**
* After deleting the relationship, the `WITH s` clause carries the `Submodel` node (`s`) to the next part of the query.
* A `WHERE` clause then applies two conditions for deletion:
    * `s.sourceUrl IS NULL`: This checks if the Submodel is a "local placeholder" and not an externally managed resource.
    * `NOT (s)<--()`: This is the crucial part. It checks if there are **no incoming relationships at all** to the Submodel node (`s`) from *any* other node. If this condition is true, it means the Submodel is no longer referenced by anything else in the database after its `HAS_SUBMODEL` link was removed.
* If both conditions are met, the Submodel node (`s`) is then **deleted**.

```cypher
WITH s
WHERE s.sourceUrl IS NULL AND NOT (s)<--()
DELETE s
```