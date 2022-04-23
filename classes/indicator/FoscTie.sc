/* ------------------------------------------------------------------------------------------------------------
• FoscTie (abjad 3.0)

------------------------------------------------------------------------------------------------------------ */
FoscTie : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <direction, <rightBroken, <tweaks;
    var <context='Voice', <persistent=true, <publishStorageFormat=true;
    *new { |direction, rightBroken=false, tweaks|
        if (direction.notNil) { direction = direction.toTridirectionalLilypondSymbol };
        ^super.new.initFoscTie(direction, rightBroken, tweaks);
    }
    initFoscTie { |argDirection, argRightBroken, argTweaks|
        direction = argDirection;
        rightBroken = argRightBroken;
        FoscLilyPondTweakManager.setTweaks(this, argTweaks);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • context

    Gets context. Returns 'Voice'.
    

    • Example 1

    a = FoscTie();
    a.context;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • direction

    Gets direction. Defaults to none.
    
    Returns up, down or none.

    
    • Example 1

    Force ties up.

    a = FoscStaff(FoscLeafMaker().(60 ! 4, [1/8]));
    m = FoscTie(direction: 'up');
    a[0..].attach(m);
    a.show;
    m.direction;


    • Example 2

    Force ties down.

    a = FoscStaff(FoscLeafMaker().(60 ! 4, [1/8]));
    m = FoscTie(direction: 'down');
    a[0..].attach(m);
    a.show;
    m.direction;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • persistent

    Is true.


    • Example 1

    a = FoscTie();
    a.persistent;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • rightBroken

    Is true when tie is right-broken.


    • Example 1

    a = FoscTie();
    a.rightBroken;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • tweaks

    Gets tweaks.


    • Example 1

    a = FoscTie();
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
    // == {
    //     ^this.notYetImplemented(thisMethod);
    // }
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
            string = "% %".format(this.direction, string);
        };
        ^string;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormatBundle
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormatBundle { |component|
        var bundle, strings, string;
        strings = [];
        bundle = FoscLilyPondFormatBundle();
        if (tweaks.notNil) {
            strings = tweaks.prListFormatContributions;
            //!!!TODO if (rightBroken) { strings = this.prTagShow(strings) };
            bundle.after.spannerStarts.addAll(strings);
        };
        string = this.prAddDirection("~");
        strings = [string];
        //!!!TODO if (rightBroken) { strings = this.prTagShow(strings) };
        bundle.after.spannerStarts.addAll(strings);
        ^bundle;
    }
}
