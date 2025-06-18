---

# Creation Process for Asset Administration Shell (AAS)

This document describes the Cypher statement used to create or update an Asset Administration Shell (AAS) and its associated elements (Asset, Submodels, and other References) in the Neo4j graph database.

The provided statement leverages **`MERGE`** clauses to ensure idempotency, meaning the query can be run multiple times without creating duplicate data. It creates nodes and relationships if they don't exist, or updates them if they do.

---

## 1. Create or Update the Asset Administration Shell (AAS) Node

**Objective:** Ensures the main AAS node exists and sets/updates its core properties.

**Logic:**
* A node with the label `:AssetAdministrationShell` and the unique `id` is **merged**. If it exists, it's matched; otherwise, it's created.
* Dynamically applies additional labels to the AAS node based on the input data.
* Sets properties like `idShort`, `sourceUrl`, and `registrationTime` if they are provided in the input.

```cypher
MERGE (a:AssetAdministrationShell { id: $shell.id })
SET a{% for eachLabel in aas.labels %}:{{eachLabel}}{% endfor %}
{% if aas.idShort -%}
SET a.idShort = $shell.idShort
{%- endif %}
{% if meta.sourceUrl -%}
SET a.sourceUrl = $shell.sourceUrl
{%- endif %}
{% if meta.registrationTime -%}
SET a.registrationTime = $shell.registrationTime
{%- endif %}
```

---

## 2. Create or Update the Associated Asset Node

**Objective:** Manages the `Asset` node linked to the AAS and establishes the `HAS_ASSET` relationship.

**Logic:**
* If `assetInformation` and `globalAssetId` are present in the input, an `Asset` node with the given `globalAssetId` is **merged**. This ensures that assets with the same ID are reused.
* Sets properties like `sourceUrl`, `assetKind`, and `assetType` on the `Asset` node if provided.
* A `HAS_ASSET` relationship is **merged** between the AAS node (`a`) and the `Asset` node.

```cypher
{% if aas.assetInformation is not null %}
{%- set assetInfo = aas.assetInformation -%}
{%- if assetInfo.globalAssetId is not null %}
MERGE (asset:Asset { id: $asset.globalAssetId })
{% if meta.sourceUrl -%}
SET asset.sourceUrl = $asset.sourceUrl
{%- endif %}
{%- endif %}
{%- if assetInfo.assetKind is not null %}
SET asset.assetKind = $asset.assetKind
{%- endif %}
{%- if assetInfo.assetType is not null %}
SET asset.assetType = $asset.assetType
{%- endif %}
MERGE (a) -[:HAS_ASSET]->(asset)
{%- endif -%}
```

---

## 3. Link Submodels to the AAS

**Objective:** Establishes `HAS_SUBMODEL` relationships between the AAS and its referenced Submodels.

**Logic:**
* Iterates through a list of `submodels` provided in the input.
* For each Submodel, if its `id` is available, a `Submodel` node with that `id` is **merged**. This means the Submodel node itself might already exist or is newly created if it's referenced for the first time.
* A `HAS_SUBMODEL` relationship is **merged** between the AAS node (`a`) and each respective `Submodel` node.

```cypher
{% if aas.submodels is not empty -%}
{% for eachSubmodel in aas.submodels %}
{% if eachSubmodel.keys is not empty and eachSubmodel.keys[0].value %}
MERGE (s_{{loop.index}}:Submodel {id: $submodels.s_{{ loop.index }}.id })
MERGE (a)-[:HAS_SUBMODEL]->(s_{{loop.index}})
{%- endif %}
{%- endfor %}
{%- endif %}
```

---

## 4. Create and Link Reference Elements

**Objective:** Manages the creation and linking of various reference elements (e.g., `ReferenceElement`, `RelationshipElement`) from the AAS.

**Logic:**
* Iterates through a list of `refs` (references) provided in the input.
* For each reference, a node is **merged** with a dynamically set label (if available) and properties (`id` or `smId`, and `idShortPath`). The choice of `id` or `smId` as the merge key depends on `idShortPath` presence, reflecting different types of referable elements.
* A relationship is **merged** from the AAS node (`a`) to the newly created/matched reference node (`r_X`).
* The relationship type is dynamically set to `HAS_SEMANTIC` or `HAS_REFERENCE` based on the `semantic` flag in the input, and a `type` property is added to the relationship.

```cypher
{% for eachRef in aas.refs %}
MERGE (r_{{ loop.index }}{%if eachRef.label is not null %}:{{ eachRef.label }}{%- endif %}{ {% if eachRef.idShortPath is null %}id{%else%}smId{%endif%}: $refs.r_{{ loop.index }}.id{% if eachRef.idShortPath is not null %}, idShortPath: $refs.r_{{ loop.index }}.idShortPath{% endif %}})
MERGE (a) -[:{% if eachRef.semantic %}HAS_SEMANTIC{% else %}HAS_REFERENCE{% endif %} {type: $refs.r_{{ loop.index }}.type} ]->(r_{{ loop.index }})
{%- endfor %}
```

---