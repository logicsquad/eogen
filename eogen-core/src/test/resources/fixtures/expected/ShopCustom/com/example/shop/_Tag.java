package com.example.shop;

/*
 * Tag: com.example.shop.Tag (com.example.shop._Tag), Tag in com.example.shop
 * model Shop, define "Hello from a define"
 * abstract=$entity.abstractEntity parent= restricting= keys=[]
 * singleTable=false genericRecord=false plural=Tags table=tag
 * primary key: id 
 * attribute 1. id: java=Integer class=NSNumber type=i external=integer width= precision= scale= column=id prototype=id nulls=false classProperty=false inherited=false flattened=false definition=
 * attribute 2. name: java=String class=NSString type= external=varchar width=100 precision= scale= column=name prototype=varchar100 nulls=false classProperty=true inherited=false flattened=false definition=
 * relationship 1. productTags -> ProductTag (EOGenericRecord) toMany=true mandatory=$relationship.mandatory owns=$relationship.ownsDestination propagates=$relationship.propagatesPrimaryKey delete=Nullify flattened=false definition= inverse=tag classProperty=$relationship.classProperty inherited=false
 * constant after id: none
 * constant after name: none
 * macros: [Tag] [Shop] * tools: false true 0
 * hyphens: allowed
 * empty is true: yes * no argument conversion: $entity.name.substring("1")
 */
public abstract class _Tag {
}
