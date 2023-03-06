/* ------------------------------------------------------------------------------------------------------------
• FoscDynamicTrend (abjad 3.0)

Dynamic trend.


• Example 1

a = FoscVoice(FoscLeafMaker().(#[60,62,64,65], 1/4));
a[0].attach(FoscDynamic('p'));
a[0].attach(FoscDynamicTrend('<'));
a[a.lastIndex].attach(FoscDynamic('f'));
a.show;


• Example 2

Set 'leftBroken' to true to initialize without starting dynamic.

a = FoscVoice(FoscLeafMaker().(#[60,62,64,65], 1/4));
a[0].attach(FoscDynamicTrend('<', leftBroken: true));
a[a.lastIndex].attach(FoscDynamic('f'));
a.show;


• Example 3

Crescendo dal niente.

a = FoscVoice(FoscLeafMaker().(#[60,62,64,65], 1/4));
a[0].attach(FoscDynamic('niente', hide: true));
a[0].attach(FoscDynamicTrend('o<'));
a[a.lastIndex].attach(FoscDynamic('f'));
a.show;


• Example 4

Decrescendo al niente.

a = FoscVoice(FoscLeafMaker().(#[60,62,64,65], 1/4));
a[0].attach(FoscDynamic('f'));
a[0].attach(FoscDynamicTrend('>o'));
a[a.lastIndex].attach(FoscDynamic('niente', command: "\\!"));
a.show;


• Example 5 !!!TODO: abjad-flared-hairpin NOT IMPLEMENTED

Subito crescendo.

a = FoscVoice(FoscLeafMaker().(#[60,62,64,65], 1/4));
a[0].attach(FoscDynamic('p'));
a[0].attach(FoscDynamicTrend('<|'));
a[a.lastIndex].attach(FoscDynamic('f'));
a.show;


• Example 6 !!!TODO: BROKEN. #constante-hairpin not implemented ??

Constante.

a = FoscVoice(FoscLeafMaker().(#[60,62,64,65], 1/4));
a[0].attach(FoscDynamic('p'));
a[0].attach(FoscDynamicTrend('--'));
a[a.lastIndex].attach(FoscDynamic('f'));
a.show;


• Example 7

DynamicTrend can be tweaked.

a = FoscVoice(FoscLeafMaker().(#[60,62,64,65], 1/4));
a[0].attach(FoscDynamic('p'));
a[0].attach(FoscDynamicTrend('<', tweaks: #[['color', 'blue']]));
a[a.lastIndex].attach(FoscDynamic('f'));
a.show;
------------------------------------------------------------------------------------------------------------ */
FoscDynamicTrend : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    classvar <knownShapes, <crescendoStart="\\<", <decrescendoStart="\\>";
    var <shape, <leftBroken, <tweaks;
    var <context='Voice', <formatSlot='after';
    *initClass {
        knownShapes = #["<", "o<", "<|", "o<|", ">", ">o", "|>", "|>o", "--"];
    }
    *new { |shape="<", leftBroken=false, tweaks|
        //assert(shape.isString);
        shape = shape.asString;
        assert(
            knownShapes.includesEqual(shape),
            "FoscDynamicTrend: not a valid shape: %.".format(shape)
        );
        ^super.new.init(shape, leftBroken, tweaks);
    }
    init { |argShape, argLeftBroken, argTweaks|
        shape = argShape;
        leftBroken = argLeftBroken;
        FoscLilyPondTweakManager.setTweaks(this, argTweaks);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • context

    Gets context. Returns 'Voice'.


    • Example 1

    m = FoscDynamicTrend('<');
    m.context.cs;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • leftBroken

    Is true when dynamic trend formats with left broken tag.


    • Example 1

    m = FoscDynamicTrend('<', leftBroken: true);
    m.leftBroken;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • shape

    Gets shape. Returns string.


    • Example 1

    m = FoscDynamicTrend('<');
    m.shape.cs;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • spannerStart
    
    Is true.


    • Example 1

    m = FoscDynamicTrend('<');
    m.spannerStart;
    -------------------------------------------------------------------------------------------------------- */
    spannerStart {
        ^true;
    }
    /* --------------------------------------------------------------------------------------------------------
    • tweaks

    Gets tweaks.


    • Example 1

    a = FoscDynamicTrend('<', tweaks: #[['color', 'blue']]);
    a.tweaks.cs;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *prConstanteHairpin
    -------------------------------------------------------------------------------------------------------- */
    *prConstanteHairpin {
        ^FoscLilyPondGrobOverride(
            grobName: "Hairpin",
            isOnce: true,
            propertyPath: "stencil",
            value: "#constante-hairpin"
        );
    }
    /* --------------------------------------------------------------------------------------------------------
    • *prCircledTip
    -------------------------------------------------------------------------------------------------------- */
    *prCircledTip {
        ^FoscLilyPondGrobOverride(
            grobName: "Hairpin",
            isOnce: true,
            propertyPath: "circled-tip",
            value: true
        );
    }
    /* --------------------------------------------------------------------------------------------------------
    • *prFlaredHairpin
    -------------------------------------------------------------------------------------------------------- */
    *prFlaredHairpin {
        ^FoscLilyPondGrobOverride(
            grobName: "Hairpin",
            isOnce: true,
            propertyPath: "stencil",
            value: "#abjad-flared-hairpin"
        );
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormat
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormat {
        var strings, override, string;
        strings = [];
        if (shape.contains("--")) {
            override = FoscDynamicTrend.prConstanteHairpin;
            string = override.tweakString;
            strings = strings.add(string);
        };
        if (shape.contains("o")) {
            override = FoscDynamicTrend.prCircledTip;
            string = override.tweakString;
            strings = strings.add(string);
        }; 
        if (shape.contains("|")) {
            override = FoscDynamicTrend.prFlaredHairpin;
            string = override.tweakString;
            strings = strings.add(string);
        }; 
        case
        { shape.contains("<") || shape.contains("--") } {
            strings = strings.add(FoscDynamicTrend.crescendoStart);
        }
        { shape.contains(">") } {
            strings = strings.add(FoscDynamicTrend.decrescendoStart);
        } {
            ^throw("%:%: bad value for shape: %.".format(this.species, thisMethod.name, shape));
        };
        //!!!TODO: if (leftBroken) { strings = FoscDynamicTrend.prTagHide(string) }; 
        ^strings;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormatBundle
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormatBundle {
        var bundle, localTweaks, strings;
        bundle = FoscLilyPondFormatBundle();
        if (tweaks.notNil) {
            localTweaks = tweaks.prListFormatContributions;
            bundle.after.spanners.addAll(localTweaks);
        };
        strings = this.prGetLilypondFormat;
        bundle.after.spanners.addAll(strings);
        ^bundle;
    }
}
