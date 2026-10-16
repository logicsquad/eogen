package com.example.shop;

/*
 * Party: com.example.shop.Party (com.example.shop._Party), Party in com.example.shop
 * model Shop, define "Hello from a define"
 * abstract=true parent= restricting= keys=[]
 * singleTable=false genericRecord=false plural=Parties table=party
 * primary key: id 
 * attribute 1. email: java=String class=NSString type= external=varchar width=100 precision= scale= column=email prototype=varchar100 nulls=$attribute.allowsNull classProperty=true inherited=false flattened=false definition=
 * attribute 2. id: java=Integer class=NSNumber type=i external=integer width= precision= scale= column=id prototype=id nulls=false classProperty=false inherited=false flattened=false definition=
 * attribute 3. name: java=String class=NSString type= external=varchar width=100 precision= scale= column=name prototype=varchar100 nulls=false classProperty=true inherited=false flattened=false definition=
 * attribute 4. status: java=com.example.shop.Status class=com.example.shop.Status type= external=varchar width=50 precision= scale= column=status prototype=javaEnum nulls=false classProperty=true inherited=false flattened=false definition=
 * attribute 5. type: java=String class=NSString type= external=varchar width=100 precision= scale= column=type prototype=varchar100 nulls=false classProperty=false inherited=false flattened=false definition=
 * relationship 1. orders -> Order (com.example.shop.Order) toMany=true mandatory=$relationship.mandatory owns=$relationship.ownsDestination propagates=$relationship.propagatesPrimaryKey delete=Nullify flattened=false definition= inverse=customer classProperty=true inherited=false
 * constant after email: none
 * constant after id: none
 * constant after name: none
 * constant after status: none
 * constant after type: none
 * macros: [Party] [Shop] * tools: false true 0
 * hyphens: allowed
 * empty is true: yes * no argument conversion: $entity.name.substring("1")
 */
public abstract class _Party {
}
