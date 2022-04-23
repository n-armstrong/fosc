/* ------------------------------------------------------------------------------------------------------------
• FoscStartPhrasingSlur (abjad 3.0)
------------------------------------------------------------------------------------------------------------ */
FoscStartPhrasingSlur : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <direction, <tweaks;
    var <context='Voice', <parameter='PHRASING_SLUR', <persistent=true, <publishStorageFormat=true;
    *new { |direction, tweaks|
        if (direction.notNil) { direction = direction.toTridirectionalLilypondSymbol };
        ^super.new.init(direction, tweaks);
    }
    init { |argDirection, argTweaks|
        direction = argDirection;
        FoscLilyPondTweakManager.setTweaks(this, argTweaks);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • context

    Gets context. Returns 'Voice'.
    

    • Example 1

    a = FoscStartPhrasingSlur();
    a.context;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • direction

    Gets direction.


    • Example 1

    a = FoscStartPhrasingSlur();
    a.direction;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • parameter

    Gets parameter. Returns 'Beam'.


    • Example 1

    a = FoscStartPhrasingSlur();
    a.parameter;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • persistent

    Is true.


    • Example 1

    a = FoscStartPhrasingSlur();
    a.persistent;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • spannerStart

    Is true.


    • Example 1

    a = FoscStartPhrasingSlur();
    a.spannerStart;
    -------------------------------------------------------------------------------------------------------- */
    spannerStart {
        ^true;
    }
    /* --------------------------------------------------------------------------------------------------------
    • tweaks

    Gets tweaks.


    • Example 1

    a = FoscStartPhrasingSlur();
    a.tweaks;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • ==

    !!!TODO

    Is true when all initialization values of Abjad value object equal the initialization values of ``argument``.
    
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
    • prAddDirection
    -------------------------------------------------------------------------------------------------------- */
    prAddDirection { |string|
        if (direction.notNil) {
            string = "% %".format(direction, string);
        };
        ^string;
    }
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
        string = this.prAddDirection("\\(");
        bundle.after.spannerStarts.add(string);
        ^bundle;
    }
}
