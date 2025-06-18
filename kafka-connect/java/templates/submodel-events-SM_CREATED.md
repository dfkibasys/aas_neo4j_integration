---

# Cypher Query Generation for Submodel Creation

This document describes the structure and logic of a Pebble template used to generate Cypher queries for creating or updating Submodels and their associated elements and references within a Neo4j graph database. The template leverages an incoming `SubmodelEvent` and a `PebbleContextMeta` object to dynamically construct the necessary graph operations.

---

## Template Structure Overview

The Pebble template orchestrates a series of `MERGE` operations to ensure that nodes and relationships are created if they don't exist, or matched if they do, preventing duplicates during the creation/update process. It handles the main Submodel node, its properties, its direct references, and then iterates through all nested Submodel Elements, managing their properties, hierarchy, and references.

Global Pebble variables are defined at the top to ensure consistent `sourceUrl` generation across all generated statements.

---

## Generated Queries

### 1. Create/Update Submodel Node and its Direct References

This query focuses on the main Submodel node and any direct `HAS_REFERENCE` or `HAS_SEMANTIC` relationships it might have to other nodes.

**Generated Query Logic (Simplified):**
```cypher
MERGE (s:Submodel { id: $submodel.id })
SET s:Label1, s:Label2, ... // Dynamically added labels
SET s.idShort = $submodel.idShort
SET s.sourceUrl = $submodel.sourceUrl
SET s.registrationTime = $submodel.registrationTime

// For each direct reference (sm.refs)
MERGE (r_index:ReferencedLabel { id: $refs.r_index.id, ... })
MERGE (s)-[:HAS_REFERENCE|HAS_SEMANTIC { type: $refs.r_index.type }]->(r_index)
```
**Description:**
* A **Submodel node** is `MERGE`d based on its unique `id`. This ensures the node is created if it's new or found if it already exists.
* **Labels** from `sm.labels` are added to the `Submodel` node using individual `SET` statements. This is a safe approach for incrementally adding labels without causing duplicate node creation if the node already exists with fewer labels.
* Properties like `idShort`, `sourceUrl`, and `registrationTime` are **set** conditionally.
* For each direct reference (`sm.refs`), a target node (`r_index`) is `MERGE`d. The target node's key is either `id` or `smId` based on the reference type.
* A **relationship** (`HAS_REFERENCE` or `HAS_SEMANTIC`) is `MERGE`d between the Submodel and its referenced node, including a `type` property from the reference.

---

### 2. Create/Update Submodel Element Nodes and their Nested References (Iterative)

This part of the template iterates through all collected `SubmodelElement`s (`smElems`), creating each element, establishing its hierarchical relationship to its parent (either the Submodel or another SubmodelElement), and handling its own outgoing references. A separate statement is generated for each `SubmodelElement`.

**Generated Query Logic (Simplified, per `eachElem` in `smElems`):**
```cypher
MERGE (e:SubmodelElement {smId: $submodel.id, idShortPath: $sme.idShortPath })
SET e:LabelA, e:LabelB, ... // Dynamically added labels

// Establish hierarchy to parent
MERGE (p:Submodel|SubmodelElement {id: $submodel.id OR smId: $submodel.id, idShortPath: $parent.idShortPath })
MERGE (p)-[:HAS_ELEMENT]->(e)

SET e.idShort = $sme.idShort
SET e.sourceUrl = $sme.sourceUrl
SET e.value = $sme.value

// For each reference from this Submodel Element (eachElem.refs)
MERGE (re_index:ReferencedLabel { id: $refs.r_index.id, ... })
MERGE (e)-[:HAS_REFERENCE|HAS_SEMANTIC { type: $refs.r_index.type }]->(re_index)
```
**Description:**
* Each **`SubmodelElement` node** (`e`) is `MERGE`d using `smId` (the Submodel's ID) and its unique `idShortPath` as key properties.
* Similar to the Submodel, **labels** from `eachElem.labels` are added via individual `SET` statements.
* The parent node (`p`) is `MERGE`d. It's either the main Submodel (if `eachElem.isRootElement` is true) or another `SubmodelElement` (identified by its `smId` and `idShortPath`).
* A **`HAS_ELEMENT` relationship** is `MERGE`d to establish the hierarchical link from the parent to the current `SubmodelElement`.
* Properties like `idShort`, `sourceUrl`, and `value` are **set** conditionally.
* For each reference originating from the `SubmodelElement` (`eachElem.refs`), a target node (`re_index`) is `MERGE`d, and a **relationship** (`HAS_REFERENCE` or `HAS_SEMANTIC`) is `MERGE`d between the `SubmodelElement` and its referenced node.

---

## Parameter Generation

The `parameters` block for each statement dynamically constructs a map of values that are passed to the Cypher query. This includes:
* `submodel.id`: The unique ID of the main Submodel.
* `submodel.idShort`: The short ID of the Submodel.
* `submodel.sourceUrl`: A constructed URL for the Submodel based on `meta.sourceUrl` and the Submodel's ID.
* `submodel.registrationTime`: The timestamp from `meta.registrationTime`.
* `refs`: A map containing parameters for each referenced node (e.g., `r_1`, `r_2`), including its `id`, `smId`, `idShortPath`, `label`, and `refType`.
* `parent.idShortPath`: For Submodel Elements, the `idShortPath` of their parent.
* `sme.idShortPath`: The unique `idShortPath` of the current Submodel Element.
* `sme.idShort`: The short ID of the Submodel Element.
* `sme.sourceUrl`: A constructed URL for the Submodel Element, extending the Submodel's URL.
* `sme.value`: The value of the Submodel Element.

---