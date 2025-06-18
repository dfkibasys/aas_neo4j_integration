---

# Deletion Process for Asset Administration Shell (AAS)

This process outlines how an Asset Administration Shell (AAS) and its associated elements (Submodels, References, Assets) are systematically removed from the database or retained as "stubs," based on their referencing by other entities.

---

## 1. Deleting or Isolating Submodels

**Objective:** Removes the direct `HAS_SUBMODEL` relationship from the AAS to the Submodel. The Submodel itself is only deleted if it's considered a "local placeholder" (has no `sourceUrl`) **and** is not referenced by other external `Identifiable` nodes (AASs with an `id`).

**Logic:**
* Find all `Submodel` nodes directly referenced by the current AAS.
* Delete the `HAS_SUBMODEL` relationship.
* If the Submodel has no `sourceUrl` **and** there are no incoming references from other AASs (whose `id` is different from the current AAS's `id`), then the Submodel is completely deleted. Otherwise, it remains.

```cypher
MATCH (a:AssetAdministrationShell {id: $shell.id})-[rel:HAS_SUBMODEL]->(s:Submodel)
DELETE rel
WITH s
WHERE s.sourceUrl IS NULL
 AND NOT EXISTS {
  MATCH (s)<-[]-(o)
  WHERE o.id IS NOT NULL AND o.id <> $shell.id
 }
DETACH DELETE s
```

---

## 2. Deleting or Isolating Outgoing References

**Objective:** Removes `HAS_REFERENCE` or `HAS_SEMANTIC` relationships originating from the AAS to other nodes (`ref`). These referenced nodes are only deleted if they are "local" and not otherwise externally referenced.

**Logic:**
* Find all nodes (`ref`) directly referenced by the AAS via `HAS_REFERENCE` or `HAS_SEMANTIC`.
* Delete the corresponding relationship.
* If the `ref` node has no `sourceUrl` **and** it has no incoming references from other `Identifiable` nodes (AASs with an `id` different from the current AAS's `id`), the `ref` node is completely deleted.

```cypher
MATCH (a:AssetAdministrationShell {id: $shell.id})-[rel:HAS_REFERENCE|HAS_SEMANTIC]->(ref)
DELETE rel
WITH ref
WHERE ref.sourceUrl IS NULL
  AND NOT EXISTS {
    MATCH (ref)<-[]-(o)
    WHERE (o.id IS NOT NULL AND o.id <> $shell.id)
 }
DETACH DELETE ref
```

---

## 3. Deleting or Stubbing Assets

**Objective:** Removes the `HAS_ASSET` relationship from the AAS to the `Asset`. The `Asset` itself is either completely deleted or retained as a "stub" (without specific properties), depending on whether other entities still point to it.

**Logic:**
* Find the `Asset` referenced by the AAS via `HAS_ASSET`.
* Delete the `HAS_ASSET` relationship.
* Count all *incoming* relationships to the `Asset`.
* **If no incoming relationships exist:** The `Asset` is completely deleted from the database (including any potentially remaining outgoing relationships).
* **If incoming relationships still exist:** Specific properties (`assetKind`, `assetType`, `sourceUrl`, `registrationTime`) of the `Asset` node are removed, but the node itself (with its `globalAssetId`) remains as a stub.

```cypher
MATCH (a:AssetAdministrationShell {id: $shell.id})-[rel:HAS_ASSET]->(asset:Asset)
DELETE rel
WITH asset
OPTIONAL MATCH (asset)<-[other]-()
WITH asset, COUNT(other) AS incoming
FOREACH (_ IN CASE WHEN incoming = 0 THEN [1] ELSE [] END |
  DETACH DELETE asset
)
FOREACH (_ IN CASE WHEN incoming > 0 THEN [1] ELSE [] END |
  REMOVE asset.assetKind, asset.assetType, asset.sourceUrl, asset.registrationTime
)
```

---

## 4. Deleting or Stubbing the Asset Administration Shell (AAS)

**Objective:** Deletes the AAS itself or reduces it to a stub if references to it still exist.

**Logic:**
* Find the specific AAS.
* Count all *incoming* relationships to the AAS.
* **If no incoming relationships exist:** The AAS is completely deleted from the database (including any potentially remaining outgoing relationships that weren't handled in previous steps).
* **If incoming relationships still exist:** Specific properties of the AAS (`idShort`, `sourceUrl`, `registrationTime`) are removed, but the AAS node (with its `id`) remains as a stub.

```cypher
MATCH (a:AssetAdministrationShell { id: $shell.id })
OPTIONAL MATCH (a)<-[r]-()
WITH a, COUNT(r) AS incoming
FOREACH (_ IN CASE WHEN incoming = 0 THEN [1] ELSE [] END |
  DETACH DELETE a
)
FOREACH (_ IN CASE WHEN incoming > 0 THEN [1] ELSE [] END |
  REMOVE a.idShort,
         a.sourceUrl,
         a.registrationTime
)
```