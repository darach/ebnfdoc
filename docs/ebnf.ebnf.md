
Combined lexer and parser grammar for a variant of EBNF
=======================================================

A simple interpretation of ISO EBNF to illustrate markdown document
comment usage.

![Version](http://img.shields.io/badge/version-0.0.1_DEMO%0A-blue.svg)



## Productions

___
> __1:__ &nbsp; **letter** **=** 

> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; "A" &nbsp; **|** &nbsp; "B" &nbsp; **|** &nbsp; "C" &nbsp; **|** &nbsp; "D" &nbsp; **|** &nbsp; "E" &nbsp; **|** &nbsp; "F" &nbsp; **|** &nbsp; "G"

> &nbsp;&nbsp;&nbsp;&nbsp; **|** "H" &nbsp; **|** &nbsp; "I" &nbsp; **|** &nbsp; "J" &nbsp; **|** &nbsp; "K" &nbsp; **|** &nbsp; "L" &nbsp; **|** &nbsp; "M" &nbsp; **|** &nbsp; "N"

> &nbsp;&nbsp;&nbsp;&nbsp; **|** "O" &nbsp; **|** &nbsp; "P" &nbsp; **|** &nbsp; "Q" &nbsp; **|** &nbsp; "R" &nbsp; **|** &nbsp; "S" &nbsp; **|** &nbsp; "T" &nbsp; **|** &nbsp; "U"

> &nbsp;&nbsp;&nbsp;&nbsp; **|** "V" &nbsp; **|** &nbsp; "W" &nbsp; **|** &nbsp; "X" &nbsp; **|** &nbsp; "Y" &nbsp; **|** &nbsp; "Z"

> &nbsp;&nbsp;&nbsp;&nbsp; **|** "a" &nbsp; **|** &nbsp; "b" &nbsp; **|** &nbsp; "c" &nbsp; **|** &nbsp; "d" &nbsp; **|** &nbsp; "e" &nbsp; **|** &nbsp; "f" &nbsp; **|** &nbsp; "g"

> &nbsp;&nbsp;&nbsp;&nbsp; **|** "h" &nbsp; **|** &nbsp; "i" &nbsp; **|** &nbsp; "j" &nbsp; **|** &nbsp; "k" &nbsp; **|** &nbsp; "l" &nbsp; **|** &nbsp; "m" &nbsp; **|** &nbsp; "n"

> &nbsp;&nbsp;&nbsp;&nbsp; **|** "o" &nbsp; **|** &nbsp; "p" &nbsp; **|** &nbsp; "q" &nbsp; **|** &nbsp; "r" &nbsp; **|** &nbsp; "s" &nbsp; **|** &nbsp; "t" &nbsp; **|** &nbsp; "u"

> &nbsp;&nbsp;&nbsp;&nbsp; **|** "v" &nbsp; **|** &nbsp; "w" &nbsp; **|** &nbsp; "x" &nbsp; **|** &nbsp; "y" &nbsp; **|** &nbsp; "z"&nbsp; **;** &nbsp; 

The rule __letter__ is the set of upper and lower cases letters from A-Z.



---

> __2:__ &nbsp; **digit** **=** 

> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; "0" &nbsp; **|** &nbsp; "1" &nbsp; **|** &nbsp; "2" &nbsp; **|** &nbsp; "3" &nbsp; **|** &nbsp; "4" &nbsp; **|** &nbsp; "5" &nbsp; **|** &nbsp; "6" &nbsp; **|** &nbsp; "7" &nbsp; **|** &nbsp; "8" &nbsp; **|** &nbsp; "9"&nbsp; **;** &nbsp; 

The rule __digit__ is the set of decimal digits



---

> __3:__ &nbsp; **symbol** **=** 

> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; "[" &nbsp; **|** &nbsp; "]" &nbsp; **|** &nbsp; "{" &nbsp; **|** &nbsp; "}" &nbsp; **|** &nbsp; "(" &nbsp; **|** &nbsp; ")" &nbsp; **|** &nbsp; "<" &nbsp; **|** &nbsp; ">"

> &nbsp;&nbsp;&nbsp;&nbsp; **|** "'" &nbsp; **|** &nbsp; '"' &nbsp; **|** &nbsp; "=" &nbsp; **|** &nbsp; "|" &nbsp; **|** &nbsp; "." &nbsp; **|** &nbsp; "," &nbsp; **|** &nbsp; ";"&nbsp; **;** &nbsp; 

The __symbol__ rule enumerates the legal punctuation tokens


---

> __4:__ &nbsp; **character** **=** 

> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  &nbsp; _letter_ &nbsp;  &nbsp; **|** &nbsp;  &nbsp; _digit_ &nbsp;  &nbsp; **|** &nbsp;  &nbsp; _symbol_ &nbsp;  &nbsp; **|** &nbsp; "_"&nbsp; **;** &nbsp; 

The __character__ rule enumerates the set of all legal tokens
excluding whitespace which is skipped.


---

> __5:__ &nbsp; **id** **=** 

> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  &nbsp; _letter_ &nbsp;  &nbsp; **,** &nbsp;  &nbsp; **\{** &nbsp;  &nbsp; _letter_ &nbsp;  &nbsp; **|** &nbsp;  &nbsp; _digit_ &nbsp;  &nbsp; **|** &nbsp; "_" &nbsp; **\}** &nbsp; &nbsp; **;** &nbsp; 

* An __id__ names a rule.
* An __id__ stats with an alphabetic letter
 * Subsequent characters may be alphabetic, digits or the underscore character



---

> __6:__ &nbsp; **term** **=** 

> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  &nbsp; **\(** &nbsp; "'" &nbsp; **,** &nbsp;  &nbsp; _character_ &nbsp;  &nbsp; **,** &nbsp;  &nbsp; **\{** &nbsp;  &nbsp; _character_ &nbsp;  &nbsp; **\}** &nbsp;  &nbsp; **,** &nbsp; "'" &nbsp; **\)** &nbsp; 

> &nbsp;&nbsp;&nbsp;&nbsp; **|**  &nbsp; **\(** &nbsp; '"' &nbsp; **,** &nbsp;  &nbsp; _character_ &nbsp;  &nbsp; **,** &nbsp;  &nbsp; **\{** &nbsp;  &nbsp; _character_ &nbsp;  &nbsp; **\}** &nbsp;  &nbsp; **,** &nbsp; '"' &nbsp; **\)** &nbsp; &nbsp; **;** &nbsp; 

* A term is a key token in the language being described



---

> __7:__ &nbsp; **lhs** **=** 

> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  &nbsp; _id_ &nbsp; &nbsp; **;** &nbsp; 

* A rule's __lhs__ provides a unique id a rule



---

> __8:__ &nbsp; **rhs** **=** 

> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  &nbsp; _id_ &nbsp; 

> &nbsp;&nbsp;&nbsp;&nbsp; **|**  &nbsp; _term_ &nbsp; 

> &nbsp;&nbsp;&nbsp;&nbsp; **|**  &nbsp; **\(** &nbsp; "[" &nbsp; **,** &nbsp;  &nbsp; _rhs_ &nbsp;  &nbsp; **,** &nbsp; "]" &nbsp; **\)** &nbsp; 

> &nbsp;&nbsp;&nbsp;&nbsp; **|**  &nbsp; **\(** &nbsp; "{" &nbsp; **,** &nbsp;  &nbsp; _rhs_ &nbsp;  &nbsp; **,** &nbsp; "}" &nbsp; **\)** &nbsp; 

> &nbsp;&nbsp;&nbsp;&nbsp; **|**  &nbsp; **\(** &nbsp; "(" &nbsp; **,** &nbsp;  &nbsp; _rhs_ &nbsp;  &nbsp; **,** &nbsp; ")" &nbsp; **\)** &nbsp; 

> &nbsp;&nbsp;&nbsp;&nbsp; **|**  &nbsp; **\(** &nbsp;  &nbsp; _rhs_ &nbsp;  &nbsp; **,** &nbsp; "|" &nbsp; **,** &nbsp;  &nbsp; _rhs_ &nbsp;  &nbsp; **\)** &nbsp; 

> &nbsp;&nbsp;&nbsp;&nbsp; **|**  &nbsp; **\(** &nbsp;  &nbsp; _rhs_ &nbsp;  &nbsp; **,** &nbsp; "," &nbsp; **,** &nbsp;  &nbsp; _rhs_ &nbsp;  &nbsp; **\)** &nbsp; &nbsp; **;** &nbsp; 

A rule's __rhs__ provides syntax of the rule.

A rule can be composed of:
* An id
* A term
* A group, option or repetition sub rule
* A set of alternatives
* A set of concatenations



---

> __9:__ &nbsp; **rule** **=** 

> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  &nbsp; _lhs_ &nbsp;  &nbsp; **,** &nbsp; "=" &nbsp; **,** &nbsp;  &nbsp; _rhs_ &nbsp;  &nbsp; **,** &nbsp; ";"&nbsp; **;** &nbsp; 

A __rule__ is simply an association of an id with a set of syntax rules



---

> __10:__ &nbsp; **grammar** **=** 

> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  &nbsp; **\{** &nbsp;  &nbsp; _rule_ &nbsp;  &nbsp; **\}** &nbsp; &nbsp; **;** &nbsp; 

* A grammar is the set of rules in a language. 
* A grammar typically has a root rule from where tokens begin to be interpreted
* Some tools, such as ANTLR, allow parsing to begin from an arbitrary rule which is
very convenient.


---

