/* ------------------------------------------------------------------------------------------------------------
• FoscStartPianoPedal (abjad 3.0)

Lilypond \sustainOn, \sostenutoOn, \unaCorda commands.
------------------------------------------------------------------------------------------------------------ */
FoscStartPianoPedal : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <type, <tweaks;
    var <context='StaffGroup', <parameter='PEDAL', <persistent=true, <publishStorageFormat=true;
    *new { |type='sustain', tweaks|
        var types;
        if (type.notNil) {
            types = #['sustain', 'sostenuto', 'corda'];
            assert(
                types.includes(type),
                "FoscStartPianoPedal:new: invalid type: '%'. Valid types are: %."
                    .format(type, types.collect { |each| each.cs }.join(", "));
            )
        };
        ^super.new.init(type, tweaks);
    }
    init { |argKind, argTweaks|
        type = argKind;
        FoscLilyPondTweakManager.setTweaks(this, argTweaks);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • context

    Gets context. Returns 'StaffGroup'.
    

    • Example 1

    a = FoscStartPianoPedal();
    a.context;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • direction

    Gets direction.


    • Example 1

    a = FoscStartPianoPedal();
    a.direction;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • parameter

    Gets parameter. Returns 'Beam'.


    • Example 1

    a = FoscStartPianoPedal();
    a.parameter;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • persistent

    Is true.


    • Example 1

    a = FoscStartPianoPedal();
    a.persistent;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • spannerStart

    Is true.


    • Example 1

    a = FoscStartPianoPedal();
    a.spannerStart;
    -------------------------------------------------------------------------------------------------------- */
    spannerStart {
        ^true;
    }
    /* --------------------------------------------------------------------------------------------------------
    • tweaks

    Gets tweaks.


    • Example 1

    a = FoscStartPianoPedal();
    a.tweaks;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • type

    Gets type.


    • Example 1

    a = FoscStartPianoPedal();
    a.type.cs;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • ==

    !!!TODO

    Is true when all initialization values of Abjad value object equal the initialization values of argument.
    
    def __eq__(self, argument) -> bool:
        return StorageFormatManager.compare_objects(self, argument)
    -------------------------------------------------------------------------------------------------------- */
    == {
        ^this.notYetImplemented(thisMethod);
    }
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
    /* --------------------------------------------------------------------------------------------------------
    • hash

    !!!TODO

    Hashes Abjad value object.

    def __hash__(self) -> int:
        hash_values = StorageFormatManager(self).get_hash_values()
        try:
            result = hash(hash_values)
        except TypeError:
            raise TypeError(f'unhashable type: {self}')
        return result
    -------------------------------------------------------------------------------------------------------- */
    hash {
        ^this.notYetImplemented(thisMethod);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormatBundle
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormatBundle { |component|
        var bundle, localTweaks, string;
        bundle = FoscLilyPondFormatBundle();
        if (tweaks.notNil) {
            localTweaks = tweaks.prListFormatContributions;
            bundle.after.spannerStarts.addAll(localTweaks);
        };
        string = switch(type,
            'corda', "\\unaCorda",
            'sostenuto', "\\sostenutoOn",
            'sustain', "\\sustainOn"
        );
        bundle.after.spannerStarts.add(string);
        ^bundle;
    }
}
