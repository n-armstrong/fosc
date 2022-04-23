/* ------------------------------------------------------------------------------------------------------------
• FoscStopPianoPedal (abjad 3.0)

Lilypond \sustainOn, \sostenutoOn, \unaCorda commands.
------------------------------------------------------------------------------------------------------------ */
FoscStopPianoPedal : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <type, <leak, <tweaks;
    var <context='StaffGroup', <parameter='PEDAL', <persistent=true, <publishStorageFormat=true;
    var <timeOrientation='right';
    *new { |type='sustain', leak=false, tweaks|
        var types;
        if (type.notNil) {
            types = #['sustain', 'sostenuto', 'corda'];
            assert(
                types.includes(type),
                "FoscStopPianoPedal:new: invalid type: '%'. Valid types are: %."
                    .format(type, types.collect { |each| each.cs }.join(", "));
            )
        };
        ^super.new.init(type, leak, tweaks);
    }
    init { |argKind, argLeak, argTweaks|
        type = argKind;
        leak = argLeak;
        FoscLilyPondTweakManager.setTweaks(this, argTweaks);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • context

    Gets context. Returns 'StaffGroup'.
    

    • Example 1

    a = FoscStopPianoPedal();
    a.context;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • direction

    Gets direction.


    • Example 1

    a = FoscStopPianoPedal();
    a.direction;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • parameter

    Gets parameter. Returns 'Beam'.


    • Example 1

    a = FoscStopPianoPedal();
    a.parameter;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • persistent

    Is true.


    • Example 1

    a = FoscStopPianoPedal();
    a.persistent;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • spannerStop

    Is true.


    • Example 1

    a = FoscStopPianoPedal();
    a.spannerStop;
    -------------------------------------------------------------------------------------------------------- */
    spannerStop {
        ^true;
    }
    /* --------------------------------------------------------------------------------------------------------
    • tweaks

    Gets tweaks.


    • Example 1

    a = FoscStopPianoPedal();
    a.tweaks;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • type

    Gets type.


    • Example 1

    a = FoscStopPianoPedal();
    a.type.cs;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString

    !!!TODO

    Gets interpreter representation.

    def __repr__(self) -> str:
        return StorageFormatManager(self).get_repr_format()
    -------------------------------------------------------------------------------------------------------- */
    asCompileString {
        ^this.notYetImplemented(thisMethod);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormatBundle
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormatBundle { |component|
        var bundle, strings, localTweaks, string;
        bundle = FoscLilyPondFormatBundle();
        strings = [];
        if (tweaks.notNil) {
            localTweaks = tweaks.prListFormatContributions;
            strings = strings.addAll(tweaks);
        };
        string = switch(type,
            'corda', "\\treCorde",
            'sostenuto', "\\sostenutoOff",
            'sustain', "\\sustainOff"
        );
        strings = strings.add(string);
        if (leak) {
            strings = strings.insert(0, "<> %".format(string));
            bundle.after.leaks.addAll(strings);
        } {
            bundle.after.spannerStops.addAll(strings); 
        };
        ^bundle;
    }
}
