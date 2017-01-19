# Snitch List Tutorial

The Snitch list is one of the main objects used in Snitch Master. It is a very useful tool that you can use to group together Snitches that meet certain criteria.

Snitches are then rendered into the world and into JourneyMap with the color of their top Snitch list.

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
* It is a **function** that returns either **true** or **false** when applied to any arbitrary Snitch
* It is written in a form loosely based on C style conditional syntax. This uses operators such as `==` (equality comparison), `!=` (inequality comparison), `&&` (logical AND), `||` (logical OR) 
* The qualifier is used to evaluate every Snitch to determine if it should be in this Snitch list
* It is checked for syntax. This means that you won't be allowed to create a Snitch list with an invalid qualifier. However, you can still write qualifiers that will never evaluate to **true** for any Snitches.


![Example Snitch List 1](http://imgur.com/SQeUGqf.png)

In this example I am creating a Snitch list to identify all Snitches on my personal Namelayer group. (The group with the same name as my account) However, this form can be applied to any Namelayer group.

You can see that the qualifier is `group == 'Mr_Little_Kitty'`. This follows the general form for an expression `variable <operator> value`. 
In this case the variable is the Namelayer group name of the given Snitch, the operator is the equality operator, and the value is the string literal "Mr_Little_Kitty".
Lets say you were to create a Namelayer group named `USA Vault` and you wanted a Snitch list for all Snitches that are on that group. Your qualifier would then be `group == 'USA Vault'`.
Maybe you want a Snitch list that has all Snitches NOT on the `USA Vault` group. Your qualifier would then be `group != 'USA Vault'`.

![Example Snitch List 2](http://imgur.com/TcBbRgo.png)

In this example I am creating a Snitch list to identify all Snitches in The End dimension. (Minecraft dimensions are referred to as worlds in Snitch Master)

You can see that the qualifier is `world == 'world_the_end'`. This follows the general form for an expression `variable <operator> value`.
In this case the variable is the name of the world of the given Snitch, the operator is the equality operator, and the value is the string literal "world_the_end" (the standard world name for The End dimension).
Lets say you wanted to create a Snitch list for all Snitches NOT in the overworld. Your variable would be the world name (world), your operator would be the inequality operator (!=), and your value would be the name of the overworld ('world'). Your qualifier would then be `world != 'world'`.

![Example Snitch List 3](http://imgur.com/dsxMrXU.png)

In this example I am creating a Snitch list to identify all Snitches that will be culled in UNDER 24 hours.

You can see that the qualifier is `cull_time <= 24.0`. This follows the general form for an expression `variable <operator> value`.
In this case the variable is the time until the Snitch is culled, the operator is the less than or equal to operator, and the value is the numeric literal for 24 hours.

