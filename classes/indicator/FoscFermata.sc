/* ------------------------------------------------------------------------------------------------------------
• FoscFermata (abjad 3.0)

Fermata.


• Example 1

A short fermata.

a = FoscScore([FoscStaff([FoscNote(60, 1/4)])]);
m = FoscFermata('shortfermata');
a[0][0].attach(m);
a.show;


• Example 2

A regular fermata.

a = FoscScore([FoscStaff([FoscNote(60, 1/4)])]);
m = FoscFermata();
a[0][0].attach(m);
a.show;


• Example 3

A long fermata.

a = FoscScore([FoscStaff([FoscNote(60, 1/4)])]);
m = FoscFermata('longfermata');
a[0][0].attach(m);
a.show;


• Example 4

A very long fermata.

a = FoscScore([FoscStaff([FoscNote(60, 1/4)])]);
m = FoscFermata('verylongfermata');
a[0][0].attach(m);
a.show;


• Example 5

Fermata can be tweaked.

a = FoscScore([FoscStaff([FoscNote(60, 1/4)])]);
m = FoscFermata('longfermata', tweaks: #[['color', 'blue']]);
a[0][0].attach(m);
a.show;
------------------------------------------------------------------------------------------------------------ */
FoscFermata : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    classvar <allowableCommands;
    var <command, <tweaks;
    var <context='Score', <formatSlot='after';
    *initClass {
        allowableCommands = #['fermata', 'longfermata', 'shortfermata', 'verylongfermata'];
    }
    *new { |command='fermata', tweaks|
        assert(
            allowableCommands.includes(command.asSymbol),
            "FoscFermata:new: invalid command argument: '%'. Valid commands are: %."
                .format(command, "".ccatList(allowableCommands)[1..]);
        );
        ^super.new.init(command, tweaks);
    }
    init { |argCommand, argTweaks|
        command = argCommand;
        FoscLilyPondTweakManager.setTweaks(this, argTweaks);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • command

    Gets command.


    • Example 1

    m = FoscFermata('longfermata');
    m.command;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • context

    Gets context.


    • Example 1

    m = FoscFermata('longfermata');
    m.context;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • tweaks

    Gets tweaks.


    • Example 1

    m = FoscFermata(tweaks: #[['color', 'blue']]);
    m.tweaks.cs;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • str

    Gets string representation of breath mark.


    • Example 1

    m = FoscFermata('longfermata');
    m.str;
    -------------------------------------------------------------------------------------------------------- */
    str {
        ^("\\" ++ command);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    /////////////////////////////////////////////////////////////////////////////////////////////////////////// 
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormat
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormat {
        ^this.str;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormatBundle
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormatBundle { |component|
        var bundle, localTweaks;
        bundle = FoscLilyPondFormatBundle();
        if (tweaks.notNil) {
            localTweaks = tweaks.prListFormatContributions;
            bundle.after.articulations.addAll(localTweaks);
        };
        bundle.after.articulations.add(this.prGetLilypondFormat);
        ^bundle;
    }
}
