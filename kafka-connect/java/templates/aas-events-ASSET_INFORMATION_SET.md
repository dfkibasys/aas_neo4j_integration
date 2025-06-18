---
# Updating Asset Information

This document outlines the Cypher statement used to update the properties of an existing Asset node. It sets or updates existing information and removes missing information to precisely reflect the asset's state as provided in an incoming event.

---

## 1. Locate the Asset Node

**Objective:** To identify the specific `Asset` node to be updated based on its connection to an Asset Administration Shell (AAS).

**Logic:**
* The query first **matches** the `AssetAdministrationShell` node using its `id` (`$shell.id`).
* It then traverses the `HAS_ASSET` relationship to find the associated `Asset` node (`a`), which is then selected for the update operation.

```cypher
MATCH (:AssetAdministrationShell {id: $shell.id })-[HAS_ASSET]->(a:Asset)
```

---

## 2. Update or Remove Asset Properties

**Objective:** To set or remove properties of the located `Asset` node according to the information provided in the event.

**Logic:**
* The **`id` property of the Asset will NOT be changed**, as it serves as the node's primary key and defines its unique identity.
* For each of the following properties (`sourceUrl`, `assetKind`, `assetType`), a conditional logic is applied:
    * **`SET a.Property = $asset.Property`**: If the value for the property is **present** (not `null`) in the `$asset` parameter of the event, the property is set or updated on the `Asset` node.
    * **`REMOVE a.Property`**: If the value for the property is **missing** (or `null`) in the `$asset` parameter of the event, the corresponding property is completely removed from the `Asset` node. This ensures the database state accurately reflects the current information.

```cypher
// Handle sourceUrl: SET if present; REMOVE if absent
{%- if $asset.sourceUrl is not null %}
SET a.sourceUrl = $asset.sourceUrl
{%- else %}
REMOVE a.sourceUrl
{%- endif %}

// Handle assetKind: SET if present; REMOVE if absent
{%- if $asset.assetKind is not null %}
SET a.assetKind = $asset.assetKind
{%- else %}
REMOVE a.assetKind
{%- endif %}

// Handle assetType: SET if present; REMOVE if absent
{%- if $asset.assetType is not null %}
SET a.assetType = $asset.assetType
{%- else %}
REMOVE a.assetType
{%- endif %}
```