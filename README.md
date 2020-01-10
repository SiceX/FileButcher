WINDOWBUILDER 
swt designer
click dx su classe -> open with -> designer


jdk 13 open?	https://jdk.java.net/archive/
NO, per via di un bug a windowbuilder non piacciono le versioni sopra la 1.8


window -> preferences -> java -> java compiler

project -> properties -> java -> java compiler

project -> properties -> java build path -> libraries -> modulepath


Le referenced libraries probabilmente avranno un pathing diverso sui vari pc per via dei workspace differenti ('C:\' , 'F:\').
Le JRE System Libraries potrebbe soffrire dello stesso problema, per questo dopo il primo commit dovranno essere ignorate come le referenced.
Se si aggiungono librerie le si pushano la prima volta e poi le si ignorano.
