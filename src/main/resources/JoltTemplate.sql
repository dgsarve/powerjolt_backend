create table JoltTemplate
(
    id          bigint auto_increment
        primary key,
    category    varchar(255)                        null,
    name        varchar(255)                        null,
    description varchar(255)                        null,
    input_json  longtext                            null,
    spec_json   longtext                            null,
    output_json longtext                            null,
    tags        varchar(255)                        null,
    timestamp   timestamp default CURRENT_TIMESTAMP not null
);

INSERT INTO powerjolt.JoltTemplate (id, category, name, description, input_json, spec_json, output_json, tags, timestamp) VALUES (1, 'Shiftr', 'Inception', 'Inception', '{
  "rating": {
    "primary": {
      "value": 3
    },
    "quality": {
      "value": 3
    }
  }
}
', '[
  {
    "operation": "shift",
    "spec": {
      "rating": {
        "primary": {
          "value": "Rating",
          "max": "RatingRange"
        },
        "*": {
          "max":   "SecondaryRatings.&1.Range",
          "value": "SecondaryRatings.&1.Value",
          "$": "SecondaryRatings.&1.Id"
        }
      }
    }
  },
  {
    "operation": "default",
    "spec": {
      "Range": 5,
      "SecondaryRatings": {
        "*": {
          "Range": 5
        }
      }
    }
  }
]
', '{
  "Rating" : 3,
  "SecondaryRatings" : {
    "quality" : {
      "Id" : "quality",
      "Value" : 3,
      "Range" : 5
    }
  },
  "Range" : 5
}
', 'test', '2024-07-14 15:44:33');
INSERT INTO powerjolt.JoltTemplate (id, category, name, description, input_json, spec_json, output_json, tags, timestamp) VALUES (2, 'Shiftr', 'Convert nested data, to prefix soup.', 'Convert nested data, to prefix soup.', '{
  "Rating": 1,
  "SecondaryRatings": {
    "Design": 4,
    "Price": 2,
    "RatingDimension3": 1
  }
}', '[
  {
    "operation": "shift",
    "spec": {
      "Rating": "rating-primary",
      "SecondaryRatings": {
        "*": "rating-&"
      }
    }
  }
]', '{
  "rating-primary" : 1,
  "rating-Design" : 4,
  "rating-Price" : 2,
  "rating-RatingDimension3" : 1
}
', 'Convert nested data, to prefix soup.', '2024-07-14 15:44:33');
INSERT INTO powerjolt.JoltTemplate (id, category, name, description, input_json, spec_json, output_json, tags, timestamp) VALUES (3, 'Shiftr', 'Convert prefix soup, to nested data.', 'Convert prefix soup, to nested data.', '{
  "rating-primary": 1,
  "rating-Price": 2,
  "rating-Design": 4,
  "rating-RatingDimension3": 1
}', '[
  {
    "operation": "shift",
    "spec": {
      "rating-primary": "Rating",
      "rating-*": "SecondaryRatings.&(0,1)"
    }
  }
]', '{
  "Rating" : 1,
  "SecondaryRatings" : {
    "Price" : 2,
    "Design" : 4,
    "RatingDimension3" : 1
  }
}
', 'Convert prefix soup, to nested data.', '2024-07-14 15:44:33');
INSERT INTO powerjolt.JoltTemplate (id, category, name, description, input_json, spec_json, output_json, tags, timestamp) VALUES (4, 'Shiftr', 'Grab LHS key values.', 'Grab LHS key values.', '{
  "rating": {
    "primary": {
      "value": 3,
      "max": 5
    },
    "quality": {
      "value": 3,
      "max": 7
    }
  }
}', '[
  {
    "operation": "shift",
    "spec": {
      "rating": {
        "*": {
          "$": "ratingNames[]"
        }
      }
    }
  }
]', '{
  "ratingNames" : [ "primary", "quality" ]
}
', 'Grab LHS key values.', '2024-07-14 15:44:33');
INSERT INTO powerjolt.JoltTemplate (id, category, name, description, input_json, spec_json, output_json, tags, timestamp) VALUES (5, 'Shiftr', 'Map to list.', 'Map to list.', '{
  "ratings": {
    "primary": 5,
    "quality": 4,
    "design": 5
  }
}', '[
  {
    "operation": "shift",
    "spec": {
      "ratings": {
        "*": {
          "$": "Ratings[#2].Name",
          "@": "Ratings[#2].Value"
        }
      }
    }
  }
]', '{
  "Ratings" : [ {
    "Name" : "primary",
    "Value" : 5
  }, {
    "Name" : "quality",
    "Value" : 4
  }, {
    "Name" : "design",
    "Value" : 5
  } ]
}', 'Grab LHS key values.', '2024-07-14 15:44:33');
INSERT INTO powerjolt.JoltTemplate (id, category, name, description, input_json, spec_json, output_json, tags, timestamp) VALUES (6, 'Shiftr', 'Map to list.', 'Map to list.', '{
  "ratings": {
    "primary": 5,
    "quality": 4,
    "design": 5
  }
}', '[
  {
    "operation": "shift",
    "spec": {
      "ratings": {
        "*": {
          "$": "Ratings[#2].Name",
          "@": "Ratings[#2].Value"
        }
      }
    }
  }
]', '{
  "Ratings" : [ {
    "Name" : "primary",
    "Value" : 5
  }, {
    "Name" : "quality",
    "Value" : 4
  }, {
    "Name" : "design",
    "Value" : 5
  } ]
}
', 'Map to list.', '2024-07-14 15:44:33');
INSERT INTO powerjolt.JoltTemplate (id, category, name, description, input_json, spec_json, output_json, tags, timestamp) VALUES (7, 'Shiftr', 'List to Map.', 'List to Map.', '{
  "Photos": [
    {
      "Id": "327703",
      "Caption": "TEST>> photo 1",
      "Url": "http://bob.com/0001/327703/photo.jpg"
    },
    {
      "Id": "327704",
      "Caption": "TEST>> photo 2",
      "Url": "http://bob.com/0001/327704/photo.jpg"
    }
  ]
}', '[
  {
    "operation": "shift",
    "spec": {
      "Photos": {
        "*": {
          "Id": "photo-&1-id",
          "Caption": "photo-&1-caption",
          "Url": "photo-&1-url"
        }
      }
    }
  }
]', '{
  "photo-0-id" : "327703",
  "photo-0-caption" : "TEST>> photo 1",
  "photo-0-url" : "http://bob.com/0001/327703/photo.jpg",
  "photo-1-id" : "327704",
  "photo-1-caption" : "TEST>> photo 2",
  "photo-1-url" : "http://bob.com/0001/327704/photo.jpg"
}
', 'List to Map.', '2024-07-14 15:44:33');
INSERT INTO powerjolt.JoltTemplate (id, category, name, description, input_json, spec_json, output_json, tags, timestamp) VALUES (8, 'Shiftr', 'On a match, apply a String default.', 'On a match, apply a String default.', '{
  "data" : {
    "1234": {
      "clientId": "12",
      "hidden": true
    },
    "1235": {
      "clientId": "35",
      "hidden": false
    }
  }
}', '[
  {
    "operation": "shift",
    "spec": {
      "data": {
        "*": {
          "hidden": {
            "true": {
              "#disabled": "clients.@(3,clientId)"
            },
            "false": {
              "#enabled": "clients.@(3,clientId)"
            }
          }
        }
      }
    }
  }
]', '{
  "clients" : {
    "12" : "disabled",
    "35" : "enabled"
  }
}
', 'On a match, apply a String default.', '2024-07-14 15:44:33');
INSERT INTO powerjolt.JoltTemplate (id, category, name, description, input_json, spec_json, output_json, tags, timestamp) VALUES (9, 'Shiftr', 'Base case simple Transpose.', 'Base case simple Transpose.', '{
  "data": {
    "clientId": "1234",
    "clientName": "Acme"
  }
}', '[
  {
    "operation": "shift",
    "spec": {
      "data": {
        "@clientName": "bookMap.@clientId"
      }
    }
  }
]', '{
  "bookMap" : {
    "1234" : "Acme"
  }
}
', 'Base case simple Transpose.', '2024-07-14 15:44:33');
INSERT INTO powerjolt.JoltTemplate (id, category, name, description, input_json, spec_json, output_json, tags, timestamp) VALUES (10, 'Shiftr', 'Complex / nested Transpose.', 'Complex / nested Transpose.', '{
  "clientsActive": true,
  "clients": {
    "Acme": {
      "clientId": "Acme",
      "index": 1
    },
    "Axe": {
      "clientId": "AXE",
      "index": 0
    }
  },
  "data": {
    "bookId": null,
    "bookName": "Enchiridion"
  }
}', '[
  {
    "operation": "shift",
    "spec": {
      "clientsActive": {
        "true": {
          "@(2,clients)": {
            "*": {
              "clientId": "clientIds[@(1,index)]"
            }
          },
          "@(2,pants)": "pants"
        },
        "data": {
          "@bookId": "books.@bookName"
        }
      }
    }
  }
]', '{
  "clientIds" : [ "AXE", "Acme" ]
}
', 'Complex / nested Transpose.', '2024-07-14 15:44:33');
INSERT INTO powerjolt.JoltTemplate (id, category, name, description, input_json, spec_json, output_json, tags, timestamp) VALUES (11, 'Shiftr', 'Escaping Shiftr special chars.', 'Escaping Shiftr special chars.', '{
  "comment" : "pulled from http://json-ld.org/playground/ example recipe.  Also, Mojitos are good.",

  "@context": {
    "name": "http://rdf.data-vocabulary.org/#name",
    "ingredient": "http://rdf.data-vocabulary.org/#ingredients",
    "yield": "http://rdf.data-vocabulary.org/#yield",
    "instructions": "http://rdf.data-vocabulary.org/#instructions",
    "step": {
      "@id": "http://rdf.data-vocabulary.org/#step",
      "@type": "xsd:integer"
    },
    "description": "http://rdf.data-vocabulary.org/#description",
    "xsd": "http://www.w3.org/2001/XMLSchema#"
  },
  "name": "Mojito",
  "ingredient": [
    "12 fresh mint leaves",
    "1/2 lime, juiced with pulp",
    "1 tablespoons white sugar",
    "1 cup ice cubes",
    "2 fluid ounces white rum",
    "1/2 cup club soda"
  ],
  "yield": "1 cocktail",
  "instructions": [
    {
      "step": 1,
      "description": "Crush lime juice, mint and sugar together in glass."
    },
    {
      "step": 2,
      "description": "Fill glass to top with ice cubes."
    },
    {
      "step": 3,
      "description": "Pour white rum over ice."
    },
    {
      "step": 4,
      "description": "Fill the rest of glass with club soda, stir."
    },
    {
      "step": 5,
      "description": "Garnish with a lime wedge."
    }
  ]
}', '[
  {
    "operation": "shift",
    "spec": {
      "\\@context": {
        "name": "&1.Name",
        "ingredient": "&1.Inputs",
        "yield": "\\@context.Makes",
        "*": "&1.&"
      },
      "name": "Name",
      "ingredient": "Inputs",
      "yield": "Makes",
      "*": "&"
    }
  }
]', '{
  "comment" : "pulled from http://json-ld.org/playground/ example recipe.  Also, Mojitos are good.",
  "@context" : {
    "Name" : "http://rdf.data-vocabulary.org/#name",
    "Inputs" : "http://rdf.data-vocabulary.org/#ingredients",
    "Makes" : "http://rdf.data-vocabulary.org/#yield",
    "instructions" : "http://rdf.data-vocabulary.org/#instructions",
    "step" : {
      "@id" : "http://rdf.data-vocabulary.org/#step",
      "@type" : "xsd:integer"
    },
    "description" : "http://rdf.data-vocabulary.org/#description",
    "xsd" : "http://www.w3.org/2001/XMLSchema#"
  },
  "Name" : "Mojito",
  "Inputs" : [ "12 fresh mint leaves", "1/2 lime, juiced with pulp", "1 tablespoons white sugar", "1 cup ice cubes", "2 fluid ounces white rum", "1/2 cup club soda" ],
  "Makes" : "1 cocktail",
  "instructions" : [ {
    "step" : 1,
    "description" : "Crush lime juice, mint and sugar together in glass."
  }, {
    "step" : 2,
    "description" : "Fill glass to top with ice cubes."
  }, {
    "step" : 3,
    "description" : "Pour white rum over ice."
  }, {
    "step" : 4,
    "description" : "Fill the rest of glass with club soda, stir."
  }, {
    "step" : 5,
    "description" : "Garnish with a lime wedge."
  } ]
}
', 'Escaping Shiftr special chars.', '2024-07-14 15:44:33');
INSERT INTO powerjolt.JoltTemplate (id, category, name, description, input_json, spec_json, output_json, tags, timestamp) VALUES (12, 'Shiftr', 'Transpose data in an Array.', 'Transpose data in an Array.', '{
  "restaurantId": "ZZ4ORJDY3E",
  "chainId": "RLR932KI",
  "orderItems": [
    {
      "itemName": "Small Barqs",
      "quantity": 2
    },
    {
      "itemName": "Mozzz",
      "quantity": 1
    }
  ]
}', '[
  // Issue from issue #116  Value to Key conversion
  // Is a good test case for @ "transpose" operator on the Right and Left hand sides
  // "@quantity": "basket_item.[].@itemName"
  {
    "operation": "shift",
    "spec": {
      "chainId": "retailer_id",
      "restaurantId": "store_id",
      "orderItems": {
        "*": {
          "quantity": "basket_item.[].@(1,itemName)"
        }
      }
    }
  }
]
', '{
  "retailer_id" : "RLR932KI",
  "store_id" : "ZZ4ORJDY3E",
  "basket_item" : [ {
    "Small Barqs" : 2
  }, {
    "Mozzz" : 1
  } ]
}
', 'Transpose data in an Array.', '2024-07-14 15:44:33');
INSERT INTO powerjolt.JoltTemplate (id, category, name, description, input_json, spec_json, output_json, tags, timestamp) VALUES (13, 'Shiftr', 'Transpose data in an Array.', 'Transpose data in an Array.', '{
  "restaurantId": "ZZ4ORJDY3E",
  "chainId": "RLR932KI",
  "orderItems": [
    {
      "itemName": "Small Barqs",
      "quantity": 2
    },
    {
      "itemName": "Mozzz",
      "quantity": 1
    }
  ]
}', '[
  {
    "operation": "shift",
    "spec": {
      "chainId": "retailer_id",
      "restaurantId": "store_id",
      "orderItems": {
        "*": {
          "quantity": "basket_item.[].@(1,itemName)"
        }
      }
    }
  }
]
', '{
  "retailer_id" : "RLR932KI",
  "store_id" : "ZZ4ORJDY3E",
  "basket_item" : [ {
    "Small Barqs" : 2
  }, {
    "Mozzz" : 1
  } ]
}
', 'Transpose data in an Array.', '2024-07-14 15:44:33');
INSERT INTO powerjolt.JoltTemplate (id, category, name, description, input_json, spec_json, output_json, tags, timestamp) VALUES (14, 'Shiftr', 'Bucket data from an Array, based on a leaf level value.', 'Bucket data from an Array, based on a leaf level value.', '{
  "entities": [
    {
      "type": "alpha",
      "data": "foo"
    },
    {
      "type": "beta",
      "data": "bar"
    },
    {
      "type": "alpha",
      "data": "zoo"
    }
  ]
}', '[
  {
    "operation": "shift",
    "spec": {
      "entities": {
        "*": "@type[]"
      }
    }
  }
]', '{
  "alpha" : [ {
    "type" : "alpha",
    "data" : "foo"
  }, {
    "type" : "alpha",
    "data" : "zoo"
  } ],
  "beta" : [ {
    "type" : "beta",
    "data" : "bar"
  } ]
}', 'Bucket data from an Array, based on a leaf level value.', '2024-07-14 15:44:33');
INSERT INTO powerjolt.JoltTemplate (id, category, name, description, input_json, spec_json, output_json, tags, timestamp) VALUES (15, 'Shiftr', 'Filter data from an Array, based on a leaf level value.', 'Filter data from an Array, based on a leaf level value.', '{
  "books": [
    {
      "title": "foo",
      "availability": [
        "online"
      ]
    },
    {
      "title": "bar",
      "availability": [
        "online",
        "paperback"
      ]
    },
    {
      "title": "baz",
      "availability": [
        "paperback"
      ]
    }
  ]
}', '[
  {
    "operation": "shift",
    "spec": {
      "books": {
        "*": {
          "availability": {
            "*": {
              "paperback": {
                "@3": "PaperBooks[]"
              }
            }
          }
        }
      }
    }
  }
]', '{
  "PaperBooks" : [ {
    "title" : "bar",
    "availability" : [ "online", "paperback" ]
  }, {
    "title" : "baz",
    "availability" : [ "paperback" ]
  } ]
}
', 'Filter data from an Array, based on a leaf level value.', '2024-07-14 15:44:33');
INSERT INTO powerjolt.JoltTemplate (id, category, name, description, input_json, spec_json, output_json, tags, timestamp) VALUES (16, 'Modify Beta', 'List Functions', 'List Functions', '{
  "scores" : [ 4, 2, 8, 7, 5 ]
}
', '[
  {
    "operation": "modify-overwrite-beta",
    "spec": {
      "numScores": "=size(@(1,scores))",
      "firstScore": "=firstElement(@(1,scores))",
      "lastScore": "=lastElement(@(1,scores))",
      "scoreAtMidPoint": "=elementAt(@(1,scores),2)",
      "sortedScores": "=sort(@(1,scores))"    }
  }
]', '{
  "scores" : [ 4, 2, 8, 7, 5 ],
  "numScores" : 5,
  "firstScore" : 4,
  "lastScore" : 5,
  "sortedScores" : [ 2, 4, 5, 7, 8 ]
}
', 'List Functions', '2024-07-14 15:44:34');
INSERT INTO powerjolt.JoltTemplate (id, category, name, description, input_json, spec_json, output_json, tags, timestamp) VALUES (17, 'Modify Beta', 'Math Functions', 'Math Functions', '{
  "intData" : [ 2, 7, 5 ],
  "doubleData" : [ 0.25, 1.5, 1 ],

  "a" : 10,
  "b" : 5,
  "c" : 3,

  "negative" : "-1.0"
}', '[
  {
    "operation": "modify-overwrite-beta",
    "spec": {
      "sumIntData": "=intSum(@(1,intData))",
      "sumLongData": "=intSum(@(1,intData))", // same as intSum but returns a Java Long
      "sumDoubleData": "=doubleSum(@(1,doubleData))",
      "avgIntData" : "=avg(@(1,intData))",    // note this returns a double
      "avgDoubleData" : "=avg(@(1,doubleData))",
      "sortedIntScores" : "=sort(@(1,intData))",
      "minAB" : "=min(@(1,a),@(1,b))",  // should be 5
      "maxAB" : "=max(@(1,a),@(1,b))",  // should be 10
      "abs" : "=abs(@(1,negative))",
      "aDivB": "=divide(@(1,a),@(1,b))",
      "aDivC": "=divide(@(1,a),@(1,c))", // will be 3.3333
      "aDivCRounded4": "=divideAndRound(4,@(1,a),@(1,c))"
    }
  }
]', 'test', 'Math Functions', '2024-07-14 15:44:34');
INSERT INTO powerjolt.JoltTemplate (id, category, name, description, input_json, spec_json, output_json, tags, timestamp) VALUES (18, 'Modify Beta', 'Type Conversion', 'Type Conversion', '{
  "happy": "true",
  "meh": "meh",
  "answer": 42,

  "statistics" : [
    {
      "id" : "A",
      "min" : "2.0",
      "max" : "10.0",
      "avg" : "7.9"
    },
    {
      "min" : "6",
      "max" : "6",
      "avg" : "6"
    },
    {
      "id" : "C"
    }
  ]
}', '[
  {
    "operation": "modify-overwrite-beta",
    "spec": {
      "happy": "=toBoolean",
      "meh": ["=toBoolean", false],
      "answer": "=toString",
      "statistics": {
        "*": {
          "min": ["=toInteger", 0],
          "max": ["=toInteger", null],
          "avg": ["=toDouble", null],
          "_id": "UNKNOWN"
        }
      }
    }
  }
]', '{
  "happy" : true,
  "meh" : false,
  "answer" : "42",
  "statistics" : [ {
    "id" : "A",
    "min" : 2,
    "max" : 10,
    "avg" : 7.9
  }, {
    "min" : 6,
    "max" : 6,
    "avg" : 6.0,
    "id" : "UNKNOWN"
  }, {
    "id" : "C",
    "min" : 0,
    "max" : null,
    "avg" : null
  } ]
}', 'Type Conversion', '2024-07-14 15:44:34');
INSERT INTO powerjolt.JoltTemplate (id, category, name, description, input_json, spec_json, output_json, tags, timestamp) VALUES (19, 'Modify Beta', 'String Concatenation', 'String Concatenation', '{
  "x" : [ 3, 2, 1, "go" ],
  "small" : "small",
  "BIG" : "BIG",
  "people" : [ {
    "firstName" : "Bob",
    "lastName" : "Smith",
    "address" : {
      "state" : "Texas"
    },
    "fullName" : "Bob Smith"
  }, {
    "firstName" : "Sterling",
    "lastName" : "Archer",
    "fullName" : "Sterling Archer"
  } ],
  "y" : "3,2,1,go",
  "z" : "3 2 1 go",
  "small_toUpper" : "SMALL",
  "BIG_toLower" : "big"
}
', '[
  {
    "operation": "modify-default-beta",
      "people": {
        "*": {
          "fullName": "=concat(@(1,firstName),,@(1,lastName))",
          //
          // Suffix of "?" means only match if the input actually has an "address"
          "address?": {
            // The transform "modify-default-beta" will only match if the
            //  "left hand side" does not exist or is null
            "state": "Texas"
          }
        }
      }
  }
]', '{
  "x" : [ 3, 2, 1, "go" ],
  "small" : "small",
  "BIG" : "BIG",
  "people" : [ {
    "firstName" : "Bob",
    "lastName" : "Smith",
    "address" : {
      "state" : "Texas"
    },
    "fullName" : "Bob Smith"
  }, {
    "firstName" : "Sterling",
    "lastName" : "Archer",
    "fullName" : "Sterling Archer"
  } ],
  "y" : "3,2,1,go",
  "z" : "3 2 1 go",
  "small_toUpper" : "SMALL",
  "BIG_toLower" : "big"
}', 'String Concatenation', '2024-07-14 15:44:34');
