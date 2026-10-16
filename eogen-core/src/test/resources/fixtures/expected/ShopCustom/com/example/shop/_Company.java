package com.example.shop;

/*
 * Company: com.example.shop.Company (com.example.shop._Company), Company in com.example.shop
 * model Shop, define "Hello from a define"
 * abstract=$entity.abstractEntity parent=Party restricting=(type = 'C') keys=[type]
 * singleTable=true genericRecord=false plural=Companies table=party
 * primary key: id 
 * attribute 1. email: java=String class=NSString type= external=varchar width=100 precision= scale= column=email prototype=varchar100 nulls=$attribute.allowsNull classProperty=true inherited=true flattened=false definition=
 * attribute 2. id: java=Integer class=NSNumber type=i external=integer width= precision= scale= column=id prototype=id nulls=false classProperty=false inherited=true flattened=false definition=
 * attribute 3. name: java=String class=NSString type= external=varchar width=100 precision= scale= column=name prototype=varchar100 nulls=false classProperty=true inherited=true flattened=false definition=
 * attribute 4. status: java=com.example.shop.Status class=com.example.shop.Status type= external=varchar width=50 precision= scale= column=status prototype=javaEnum nulls=false classProperty=true inherited=true flattened=false definition=
 * attribute 5. taxNumber: java=String class=NSString type= external=varchar width=100 precision= scale= column=tax_number prototype=varchar100 nulls=$attribute.allowsNull classProperty=true inherited=false flattened=false definition=
 * attribute 6. type: java=String class=NSString type= external=varchar width=100 precision= scale= column=type prototype=varchar100 nulls=false classProperty=false inherited=true flattened=false definition=
 * relationship 1. orders -> Order (com.example.shop.Order) toMany=true mandatory=$relationship.mandatory owns=$relationship.ownsDestination propagates=$relationship.propagatesPrimaryKey delete=Nullify flattened=false definition= inverse= classProperty=true inherited=true
 * constant after email: none
 * constant after id: none
 * constant after name: none
 * constant after status: none
 * constant after taxNumber: none
 * constant after type: none
 * macros: [Company] [Shop] * tools: false true 1
 * hyphens: allowed
 * empty is true: yes * no argument conversion: $entity.name.substring("1")
 */
public abstract class _Company {
}
