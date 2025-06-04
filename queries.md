Quality Check of Semantic Reference Assignments for Properties
```
MATCH (:Property)-[r:HAS_REFERENCE]->(sc:SemanticConcept)
WITH
  count(r) AS totalRefs,
  count(CASE WHEN sc.id IS NOT NULL AND sc.id <> '' AND NOT sc.id CONTAINS 'SemanticIdNotAvailable' THEN 1 END) AS validSemanticRefs,
  count(CASE WHEN sc.id IS NULL OR sc.id = '' THEN 1 END) AS noSemanticId,
  count(CASE WHEN sc.id CONTAINS 'SemanticIdNotAvailable' THEN 1 END) AS placeholderSemantic
RETURN
  totalRefs,
  validSemanticRefs,
  noSemanticId,
  placeholderSemantic,
  CASE WHEN totalRefs = 0 THEN 0 ELSE validSemanticRefs * 1.0 / totalRefs END AS validSemanticRatio
```

Query to Display All Nodes and Relationships Except Placeholder SemanticConcepts
```
MATCH (n)-[r]->(m)
WHERE NOT (
  m:SemanticConcept AND (
    m.id CONTAINS 'SemanticIdNotAvailable' OR
    m.id IN [
      'https://admin-shell.io/sandbox/SG2/TechnicalData/MainSection/1/1',
      'https://admin-shell.io/SubmodelTemplates/ExampleValue/1/0',
      'https://admin-shell.io/idta/HierarchicalStructures/HasPart/1/0',
      'https://admin-shell.io/idta/HierarchicalStructures/Node/1/0', 
      'https://admin-shell.io/SubmodelTemplates/Cardinality/1/0'

    ]
  )
)
RETURN n, r, m
```

Compact Query Excluding SemanticConcept Nodes and Their Relationships
```
MATCH (n)-[r]->(m)
WHERE NOT m:SemanticConcept
RETURN n, r, m
```

Which concepts are used: 
```
MATCH (sc:SemanticConcept)
RETURN DISTINCT sc.id AS semanticId
ORDER BY semanticId
```

How often are these semantic concepts referenced:
```
MATCH (:Property)-[r:HAS_SEMANTIC]->(sc:SemanticConcept)
RETURN sc.id AS semanticId, count(r) AS referenceCount
ORDER BY referenceCount DESC
```



match(n:Submodel {idShort: "Identification"})-[p]->(t) return n,p,t

match(a:AssetAdministrationShell)-[:HAS_SUBMODEL]->(s:Submodel)-[p:HAS_SEMANTIC]->(SemanticConcept {id:'https://www.hsu-hh.de/aut/aas/identification'}) 
match(s)-[:HAS_ELEMENT]->(pMf)-[:HAS_SEMANTIC]->(:SemanticConcept {id: '0173-1#02-AAO677#002'})
match(s)-[:HAS_ELEMENT]->(pPd)-[:HAS_SEMANTIC]->(:SemanticConcept {id: '0173-1#02-AAW338#001'})
match(s)-[:HAS_ELEMENT]->(pVers)-[:HAS_SEMANTIC]->(:SemanticConcept {id: 'www.company.com/ids/cd/2190_5082_7091_3557'})
WHERE pPd.value = 'MiR100'
return distinct a, s, pMf, pPd, pVers


MATCH (a:AssetAdministrationShell)-[:HAS_SUBMODEL]->(s:Submodel)
MATCH (s)-[:HAS_ELEMENT]->(pMf)-[:HAS_SEMANTIC]->(:SemanticConcept {id: '0173-1#02-AAO677#002'})
MATCH (s)-[:HAS_ELEMENT]->(pPd)-[:HAS_SEMANTIC]->(:SemanticConcept {id: '0173-1#02-AAW338#001'})
MATCH (s)-[:HAS_ELEMENT]->(pVers)-[:HAS_SEMANTIC]->(:SemanticConcept {id: '	0173-1#02-AAS383#003'})
RETURN DISTINCT 
  a.id AS AAS, 
  a.sourceUrl AS Source, 
  pMf.value AS Manufacturer, 
  pPd.value AS ProductDesignation, 
  pVers.value AS SoftwareVersion

' all dangling references of an element '
MATCH (source)-[r:HAS_REFERENCE]->(target) 
WHERE target.sourceUrl IS NULL 
return source.sourceUrl AS sourceUrl , r.type AS refType, target.id AS targetId, target.idShortPath AS targetIdShortPath



MATCH (a:AssetAdministrationShell)-[hs:HAS_SUBMODEL]->(s)-[R:HAS_ELEMENT*]->(e:Entity)-[r:HAS_REFERENCE]->(target)
WHERE target.sourceUrl IS NULL
RETURN a, s, e, r, target,R,hs


match (n:Property {smId: "http://aas.dfki.de/ids/sm/identification_10000000", idShortPath: "SoftwareRevision"}) return n.value




filter by sortware version:

MATCH (a:AssetAdministrationShell)-[:HAS_SUBMODEL]->(s:Submodel)
MATCH (s)-[:HAS_ELEMENT]->(pMf)-[:HAS_SEMANTIC]->(:SemanticConcept {id: '0173-1#02-AAO677#002'})
MATCH (s)-[:HAS_ELEMENT]->(pPd)-[:HAS_SEMANTIC]->(:SemanticConcept {id: '0173-1#02-AAW338#001'})
MATCH (s)-[:HAS_ELEMENT]->(pVers)-[:HAS_SEMANTIC]->(:SemanticConcept {id: '0173-1#02-AAS383#003'})
WHERE pPd.value = 'MiR100'
WITH a, s, pMf, pPd, pVers, 
  split(pVers.value, ".") 
  AS versionParts
WITH a, s, pMf, pPd, pVers,
  toInteger(versionParts[0]) AS major,
  toInteger(versionParts[1]) AS minor,
  toInteger(versionParts[2]) AS patch
WHERE
  (major < 3) OR
  (major = 3 AND minor < 5) OR
  (major = 3 AND minor = 5 AND patch < 2)
RETURN a.id