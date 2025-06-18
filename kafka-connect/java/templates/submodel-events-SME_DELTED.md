---

# Neo4j Cypher Scripts for AAS Submodel Element Management

This README outlines a set of Cypher scripts designed to manage `SubmodelElement` nodes and their associated references (`HAS_REFERENCE`, `HAS_SEMANTIC`) and hierarchical connections (`HAS_ELEMENT`) within a Neo4j graph database, following the Asset Administration Shell (AAS) metamodel.

The scripts perform a targeted "garbage collection" and cleanup operation, ensuring that only specific, unwanted references are removed and that `SubmodelElement` nodes are deleted only when they are truly "orphaned" according to defined criteria.

---

## 1. Core Logic & Assumptions

These scripts are built upon a precise definition of what constitutes a "valid" or "undesirable" incoming reference to a `SubmodelElement` (`n` or `t`):

* **`HAS_ELEMENT` relationships:** These define the internal hierarchical structure of a Submodel (e.g., `parent -[:HAS_ELEMENT]-> child`). They are *not* considered "protective" external references in this context. If a node is only connected via `HAS_ELEMENT` (and its parent is being deleted or removed), it's considered for deletion.
* **`HAS_REFERENCE` relationships:** These are considered the primary "protective" external references. If a node has an incoming `HAS_REFERENCE` from an "undesirable" source, it will **not** be deleted.
* **`HAS_SEMANTIC` relationships:** These are treated as references that can be deleted if originating from the specified deletion path. They are *not* considered "protective" in the same way `HAS_REFERENCE` relationships are in the `NOT EXISTS` checks.
* **"Undesirable" Incoming `HAS_REFERENCE` sources:**
    * Any `Identifiable` node (e.g., `AssetAdministrationShell`, any `Submodel` node, including the root of the current `Submodel`).
    * Any `SubmodelElement` node whose `smId` is different from the current `$submodel.id`.
    * Any `SubmodelElement` node within the *current* `$submodel.id` whose `idShortPath` does **not** start with the specified `$sme.idShortPath`.
* **`t.sourceUrl IS NULL`:** Only `t` nodes that do not have a `sourceUrl` property are considered for deletion, preventing the removal of nodes representing external (stub) references.

---

## 2. Cypher Statements

The process is broken down into three sequential statements, which should be executed in order.

### Statement 1: Delete Specific References from the Target Path

This statement identifies `HAS_REFERENCE` or `HAS_SEMANTIC` relationships originating from the specified `$sme.idShortPath` hierarchy (`e` and its descendants `n`) and deletes them. It then proceeds to delete the referenced target nodes (`t`) if they meet the "orphaned" criteria (no `sourceUrl` and no incoming `HAS_REFERENCE` from undesirable sources).

```cypher
- statement: |
    MATCH (e:SubmodelElement {smId: $submodel.id})
      -[r:HAS_ELEMENT*0..]->(n:SubmodelElement)
      -[ref:HAS_REFERENCE|HAS_SEMANTIC]->(t)
    WHERE e.idShortPath STARTS WITH $sme.idShortPath
    DELETE ref
    WITH t
    WHERE t.sourceUrl IS NULL AND NOT EXISTS {
      (t)<-[:HAS_REFERENCE]-(o)
      WHERE (o:Identifiable)
        OR (o:SubmodelElement AND (o.smId <> $submodel.id
          OR NOT o.idShortPath STARTS WITH $sme.idShortPath))
    }
    DETACH DELETE t
  parameters:
    submodel:
      id: "{{ event.smId }}"
    sme:
      idShortPath: "{{ event.smElementPath }}"
```

**Explanation:**
* **`MATCH (e)-[r:HAS_ELEMENT*0..]->(n)-[ref:HAS_REFERENCE|HAS_SEMANTIC]->(t)`**: Finds paths starting from `e` (the specified root of the path hierarchy, e.g., "L"), through its children/descendants `n` (including `e` itself if `r` has length 0), to a target `t` via `HAS_REFERENCE` or `HAS_SEMANTIC` relationships.
* **`WHERE e.idShortPath STARTS WITH $sme.idShortPath`**: Filters `e` to ensure it's the specific starting point for the cleanup.
* **`DELETE ref`**: Removes the matched `HAS_REFERENCE` or `HAS_SEMANTIC` relationships immediately.
* **`WITH t`**: Passes the target nodes `t` to the next clause.
* **`WHERE t.sourceUrl IS NULL AND NOT EXISTS { (t)<-[:HAS_REFERENCE]-(o) WHERE ... }`**: This is the core "orphan" check for `t`. It ensures `t` has no `sourceUrl` AND no incoming `HAS_REFERENCE` from an undesirable source (as defined above).
* **`DETACH DELETE t`**: If `t` meets the criteria, it's deleted, along with any of its outgoing relationships.

---

### Statement 2: Clean Up Properties of Referenced Elements

This statement removes `value` and `sourceUrl` properties from `SubmodelElement` nodes (`n`) that are part of the specified hierarchy (`$sme.idShortPath`) but *cannot* be deleted because they are still referenced by an undesirable incoming `HAS_REFERENCE`.

```cypher
- statement: |
    MATCH (e:SubmodelElement {smId: $submodel.id})
      -[r:HAS_ELEMENT*0..]->(n:SubmodelElement)
    WHERE n.idShortPath STARTS WITH $sme.idShortPath
      AND EXISTS {
      (n)<-[:HAS_REFERENCE]-(o)
      WHERE (o:Identifiable)
        OR (o:SubmodelElement AND (o.smId <> $submodel.id
          OR NOT o.idShortPath STARTS WITH $sme.idShortPath))
    }
    REMOVE n.value, n.sourceUrl
  parameters:
    submodel:
      id: "{{ event.id }}"
    sme:
      idShortPath: "{{ event.smElementPath }}"
```

**Explanation:**
* **`MATCH (e)-[r:HAS_ELEMENT*0..]->(n)`**: Identifies `SubmodelElement` nodes `n` within the target hierarchy.
* **`WHERE n.idShortPath STARTS WITH $sme.idShortPath`**: Ensures `n` is within the relevant path.
* **`AND EXISTS { (n)<-[:HAS_REFERENCE]-(o) WHERE ... }`**: Checks if `n` *has* an incoming `HAS_REFERENCE` from an undesirable source.
* **`REMOVE n.value, n.sourceUrl`**: If such a reference exists, `n`'s `value` and `sourceUrl` properties are removed, effectively "emptying" it but keeping the node itself due to external dependencies.

---

### Statement 3: Delete Orphaned Elements

This final statement targets `SubmodelElement` nodes (`n`) within the specified hierarchy that are no longer referenced by any undesirable incoming `HAS_REFERENCE` and can therefore be safely deleted.

```cypher
- statement: |
    MATCH (e:SubmodelElement {smId: $submodel.id})
      -[r:HAS_ELEMENT*0..]->(n:SubmodelElement)
    WHERE n.idShortPath STARTS WITH $sme.idShortPath
      AND NOT EXISTS {
      (n)<-[:HAS_REFERENCE]-(o)
      WHERE (o:Identifiable)
        OR (o:SubmodelElement AND (o.smId <> $submodel.id
          OR NOT o.idShortPath STARTS WITH $sme.idShortPath))
    }
    DETACH DELETE n
  parameters:
    submodel:
      id: "{{ event.id }}"
    sme:
      idShortPath: "{{ event.smElementPath }}"
```

**Explanation:**
* **`MATCH (e)-[r:HAS_ELEMENT*0..]->(n)`**: Identifies `SubmodelElement` nodes `n` within the target hierarchy.
* **`WHERE n.idShortPath STARTS WITH $sme.idShortPath`**: Ensures `n` is within the relevant path.
* **`AND NOT EXISTS { (n)<-[:HAS_REFERENCE]-(o) WHERE ... }`**: Checks if `n` *does NOT have* any incoming `HAS_REFERENCE` from an undesirable source. This means `n` is considered "orphaned" (or only held by `HAS_ELEMENT` from its parent).
* **`DETACH DELETE n`**: If `n` meets these criteria, it's deleted along with all its relationships (both incoming and outgoing `HAS_ELEMENT`, `HAS_REFERENCE`, `HAS_SEMANTIC`, etc.). This effectively removes the node from the graph.

---

## 3. Usage

These statements are designed to be executed sequentially within a transaction or as part of a larger cleanup routine.

**Parameters:**

* `$submodel.id`: The `smId` of the current `Submodel` (e.g., `"http://test.sm"`). This is used to scope the operations to a specific Submodel.
* `$sme.idShortPath`: The `idShortPath` of the `SubmodelElement` that marks the root of the hierarchy you intend to clean up (e.g., `"L"`). All elements `n` whose `idShortPath` starts with this value will be considered.

By running these scripts, you can maintain a clean and consistent Neo4j graph representation of your AAS Submodels, ensuring that elements and their references are managed according to your specific rules for ownership and dependency.