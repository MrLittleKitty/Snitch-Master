# Snitch List Tutorial

The Snitch list is one of the main objects used in Snitch Master. It is a very useful tool that you can use to group together Snitches that meet certain criteria.

Snitches are then displayed in the world and io JourneyMap with the color of their top Snitch list.

This tutorial will help you with learning to use Snitch list qualifier language so you can group your Snitches together however you like.

The following examples and an explanation for each should help you understand the syntax of the language and how to write your own qualifiers. A list of operators, variables, and syntax details can be found at the bottom of this page.

## Snitch List GUI

![New Snitch List Screen](http://imgur.com/RTCM3mA.png)

Pictured above is the GUI that you will use to create a Snitch list.
The fields are:
* Name: A string that will be the name of the Snitch list
* Qualifier: A function that determines whether any arbitrary Snitch will be in this list **(This is what we will be focusing on)**
* Red: A whole number value from 0 - 255 representing the red component of this list's color
* Green: A whole number value from 0 - 255 representing the green component of this list's color
* Blue: A whole number value from 0 - 255 representing the blue component of this list's color

Things to note about the Snitch list qualifier:
* It is an **expression** that evaluates to either **true** or **false** when applied to any arbitrary Snitch
* It is written in a form loosely based on C style conditional syntax. This uses operators such as `==` (equality comparison), `!=` (inequality comparison), `&&` (logical AND), `||` (logical OR) 
* The qualifier is used to evaluate every Snitch to determine if it should be in this Snitch list
* It is checked for syntax. This means that you won't be allowed to create a Snitch list with an invalid qualifier. However, you can still write qualifiers that will never evaluate to **true** for any Snitches.

### Example 1

![Example Snitch List 1](http://imgur.com/SQeUGqf.png)

In this example I am creating a Snitch list to identify all Snitches on my personal Namelayer group. (The group with the same name as my account) However, this form can be applied to any Namelayer group.

You can see that the qualifier is `group == 'Mr_Little_Kitty'`. This follows the general form for an expression: `variable <operator> value`. 
In this case the variable is the Namelayer group name of the given Snitch, the operator is the equality operator, and the value is the string literal "Mr_Little_Kitty".
Lets say you were to create a Namelayer group named `USA Vault` and you wanted a Snitch list for all Snitches that are on that group. Your qualifier would then be `group == 'USA Vault'`.
Maybe you want a Snitch list that has all Snitches NOT on the `USA Vault` group. Your qualifier would then be `group != 'USA Vault'`.

### Example 2

![Example Snitch List 2](http://imgur.com/TcBbRgo.png)

In this example I am creating a Snitch list to identify all Snitches in The End dimension. (Minecraft dimensions are referred to as worlds in Snitch Master)

You can see that the qualifier is `world == 'world_the_end'`. This follows the general form for an expression: `variable <operator> value`.
In this case the variable is the name of the world of the given Snitch, the operator is the equality operator, and the value is the string literal "world_the_end" (the standard world name for The End dimension).
Lets say you wanted to create a Snitch list for all Snitches NOT in the overworld. Your variable would be the world name (world), your operator would be the inequality operator (!=), and your value would be the name of the overworld ('world'). Your qualifier would then be `world != 'world'`.

### Example 3

![Example Snitch List 3](http://imgur.com/dsxMrXU.png)

In this example I am creating a Snitch list to identify all Snitches that will be culled in UNDER 24 hours.

You can see that the qualifier is `cull_time <= 24.0`. This follows the general form for an expression: `variable <operator> value`.
In this case the variable is the time until the Snitch is culled, the operator is the less than or equal to operator, and the value is the numeric literal for 24 hours.
This same type of qualifier can be applied using any of the numerical comparison operators (`==, !=, <, >, <=, >=`) with the cull time of a Snitch.
Note that the unit for `cull_time` is hours. That means the value 1.5 is 1 and a half hours, the value 24.0 is exactly 1 day, etc.

### Example 4

![Example Snitch List 4](http://imgur.com/8u1VVs7.png)

In this example I am creating a Snitch list to identify all Snitches that are in my land claim. For this example we will say my land claim is a square from 0 to 1000 on both the x and z axis.

You can see the start of the qualifier in the image above. The full qualifier is `x > 0 && x < 1000 && z > 0 && z < 1000`. This follows the general form for an expression: `variable <operator> value` where 4 expressions are joined together with the `&&` (logical AND) operator.
This brings up an important part of writing complex qualifiers: joining multiple expressions using either `&&` or `||`. The above quantifier uses `&&` which is the AND operator. This means that a Snitch will not pass the qualifier unless both sides of the `&&` operator evaluate to **true**.
Likewise, when using the `||` (OR) operator, a Snitch will pass the qualifier if either side of the operator evaluates to **true**.
As an example, if you want to make a Snitch list that includes Snitches on the Namelayer group `USA Vault` or the Namelayer group `USA Bunker`, your qualifier would look like this: 
`group == 'USA Vault' || group == 'USA Bunker'`.

### Note about Snitch List order

![Snitch List Ordering](http://imgur.com/IG2vEDy.png)

Pictured above is the GUI for ordering and modifying your existing Snitch lists. The 'Up' and 'Down' arrows in the 'Controls' column on the left will change the display order of the specified Snitch list.

An important aspect of Snitch lists is their display order. Snitch list display priority is from the top of the GUI to the bottom; Snitch lists at the top display over Snitch lists at the bottom. This is useful to determine what color your Snitches will be displayed as. 
For example, say you have 2 Snitch lists: 'All', which contains every Snitch and 'USA Vault' which contains every Snitch on the Namelayer group 'USA Vault'. When you encounter a Snitch in the world that is in both of these lists, the display order of the lists will determine what color the Snitch is displayed as.
Snitches will display with the color of the **HIGHEST** list that they are apart of. If a Snitch is not displaying as the color you want, make sure to check the order of your Snitch lists.


## Snitch List Qualifier Syntax

Below are tables with all the variables and operators available in the Snitch List Qualifier Language. Following the tables are some notes about the language.

| Variable | Variable Description | Variable Type | Example Of Use |
|---|
| Name | The name of the Snitch | String | `name == 'Town Farm Snitch'` |
| Group | The Namelayer group of the Snitch | String | `group == 'USA Vault'` |
| Origin | The Origin of the Snitch | String | `origin == 'jalist'` |
| World | The world that the Snitch is in | String | `world == 'world_nether'` |
| Cull_Time | The time left to cull for this Snitch | Decimal | `cull_time <= 24.0` |
| X | The X coordinate of the Snitch | Integer | `x >= 0 && x <= 1337` |
| Y | The Y coordinate of the Snitch | Integer | `y >= 60` |
| Z | The Z coordinate of the Snitch | Integer | `z >= 0 && z <= 1337` |

| Operators | Description | Applicable Types |
|---|
| == | Checks if the two operands are equal | Integer, Decimal, String |
| != | Checks if the two operands are not equal | Integer, Decimal, String |
| > | Checks if the left operand is greater than the right operand | Integer, Decimal |
| < | Checks if the left operand is less than the right operand | Integer, Decimal |
| >= | Checks if the left operand is less than or equal to the right operand | Integer, Decimal |
| <= | Checks if the left operand is greater than or equal to the right operand | Integer, Decimal |
| \|\| | Checks if either the left expression or the right expression are true | Expression |
| && | Checks if both the left expression and the right expression are true | Expression |

* Decimal types can be used with or without a decimal point. Example: `24` and `24.0` are both valid Decimal values.
* Integer types can NOT be used with a decimal point. Example: 1000.0 is not a valid Integer value.
* String literals must be enclosed in single quotes. (' ') Example: `'USA Vault'` is a valid String literal where as `USA Vault` is not.
* It is generally good practice to keep the variable as the left operand and your value as the right operand.
