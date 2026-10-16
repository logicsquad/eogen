package com.example.shop;

/*
 * Product: com.example.shop.Product (com.example.shop._Product), Product in com.example.shop
 * model Shop, define "Hello from a define"
 * abstract=$entity.abstractEntity parent= restricting= keys=[]
 * singleTable=false genericRecord=false plural=Products table=product
 * primary key: id 
 * attribute 1. active: java=Boolean class=java.lang.Boolean type=S external=varchar width=5 precision= scale= column=active prototype=boolean nulls=false classProperty=true inherited=false flattened=false definition=
 * attribute 2. category: java=Integer class=NSNumber type=i external=integer width= precision= scale= column=category prototype=intNumber nulls=$attribute.allowsNull classProperty=true inherited=false flattened=false definition=
 * attribute 3. id: java=Integer class=NSNumber type=i external=integer width= precision= scale= column=id prototype=id nulls=false classProperty=false inherited=false flattened=false definition=
 * attribute 4. name: java=String class=NSString type= external=varchar width=100 precision= scale= column=name prototype=varchar100 nulls=false classProperty=true inherited=false flattened=false definition=
 * attribute 5. price: java=java.math.BigDecimal class=NSDecimalNumber type=B external=numeric width= precision=12 scale=2 column=price prototype=amount nulls=false classProperty=true inherited=false flattened=false definition=
 * relationship 1. productTags -> ProductTag (EOGenericRecord) toMany=true mandatory=$relationship.mandatory owns=true propagates=true delete=Cascade flattened=false definition= inverse=product classProperty=$relationship.classProperty inherited=false
 * relationship 2. tags -> Tag (com.example.shop.Tag) toMany=true mandatory=$relationship.mandatory owns=$relationship.ownsDestination propagates=$relationship.propagatesPrimaryKey delete=Nullify flattened=true definition=productTags.tag inverse= classProperty=true inherited=false
 * constant after active: none
 * constant after category: com.example.shop.Category
 * constant after id: com.example.shop.Category
 * constant after name: com.example.shop.Category
 * constant after price: com.example.shop.Category
 * macros: [Product] [Shop] * tools: false true 0
 * hyphens: allowed
 * empty is true: yes * no argument conversion: $entity.name.substring("1")
 */
public abstract class _Product {
}
