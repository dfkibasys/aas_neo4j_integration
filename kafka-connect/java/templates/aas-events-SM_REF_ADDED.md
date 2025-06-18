---


# Creating a Submodel Reference for an Existing Asset Administration Shell (AAS)

This document describes the Cypher statement used to establish a `HAS_SUBMODEL` relationship between an existing Asset Administration Shell (AAS) and an existing (or newly referenced) Submodel in the Neo4j graph database.

This process is designed to simply link an AAS to a Submodel, assuming both may already exist. It uses **`MERGE`** for idempotency.

---

## 1. Create or Link the Asset Administration Shell (AAS) Node

**Objective:** Ensures the main AAS node exists. It will be matched if it already exists, or created if it's referenced for the very first time (though typically it's expected to exist for this operation).

**Logic:**
* A node with the label `:AssetAdministrationShell` and the unique `id` provided in `$shell.id` is **merged**.

```cypher
MERGE (a:AssetAdministrationShell { id: $shell.id })
```

---

## 2. Create or Link the Submodel Node

**Objective:** Ensures the target Submodel node exists. Like the AAS, it will be matched if it already exists or created if this is its first reference in the database.

**Logic:**
* A node with the label `:Submodel` and the unique `id` provided in `$submodel.id` is **merged**.

```cypher
MERGE (s:Submodel { id: $submodel.id })
```

---

## 3. Establish the HAS_SUBMODEL Relationship

**Objective:** Creates the `HAS_SUBMODEL` relationship between the AAS and the Submodel, if it doesn't already exist.

**Logic:**
* A `HAS_SUBMODEL` relationship is **merged** between the AAS node (`a`) and the Submodel node (`s`). This ensures that only one such relationship exists between these two specific nodes.

```cypher
MERGE (a)-[:HAS_SUBMODEL]->(s)
```

---