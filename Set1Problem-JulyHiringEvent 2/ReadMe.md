
# FamilyTree

## What it does (so far)
- It supports two operations ADD_CHILD, GET_RELATIONSHIP
- For name with spaces, please try `GET_RELATIONSHIP King\ Shah Son`, which supports only for get operation as of now

## Implementations
- relation_mapper.json is the json file, which has relationships that are possible in the given problem
``
"Maternal-Uncle": [
      {
        "relationType": "PARENT",
        "gender": "FEMALE"
      },
      {
        "relationType": "SIBLING",
        "gender": "MALE"
      }
    ]
- shah_family_tree.json is json file, which has the family tree structure in json format
    ` "Yodhan": {
           "name": "Yodhan",
           "gender": "MALE",
           "relations": {
             "PARENT": [
               "Dritha",
               "JATA"
             ]
           }
         }`


Relation type is classified in to four: CHILDREN, PARENT, SPOUSE, SIBLING
with Gender `Male, Female`, all the other relation is achieved based on the combination of relation types with gender


## Build
- Run `./gradlew clean build`
- Jar file location is : `./build/libes/geektrust.jar`
- execute `java -jar ./geektrust.jar {input-file}`


