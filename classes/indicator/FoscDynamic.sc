/* ------------------------------------------------------------------------------------------------------------
• FoscDynamic (abjad 3.0)

Dynamic.


• Example 1

Initialize from dynamic name.

a = FoscVoice(FoscLeafMaker().(#[60,62,64,65], 1/8));
d = FoscDynamic('f');
a[0].attach(d);
a.show;


• Example 2 !!!TODO: BROKEN

Initialize niente.

a = FoscVoice(FoscLeafMaker().(#[60,62,64,65], 1/8));
d = FoscDynamic('niente');
a[0].attach(d);
a.show;


• Example 3

Simultaneous dynamics in a single staff.

a = FoscVoice(FoscLeafMaker().(#[62,67,64,69], 1/8));
b = FoscVoice([FoscNote(60, 2/4)]);
a[0].attach(FoscLilypondLiteral("\\voiceOne"));
b[0].attach(FoscLilypondLiteral("\\voiceTwo"));
a[0].attach(FoscDynamic('f'));
override(a).dynamicLineSpanner.direction = 'up';
b[0].attach(FoscDynamic('p'));
c = FoscStaff([a, b], isSimultaneous: true);
c.show;


• Example 4

Dynamics can be tweaked.

a = FoscNote(60, 1/4);
d = FoscDynamic('f', tweaks: #[['color', 'blue']]);
a.attach(d);
a.show;

FoscDynamic('!')
------------------------------------------------------------------------------------------------------------ */
FoscDynamic : FoscObject {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    classvar <dynamicNames, <lilypondDynamicCommands;
    classvar compositeDynamicNameToSteadyStateDynamicName;
    classvar dynamicNameToDynamicOrdinal, dynamicNameToScalar, dynamicOrdinalToDynamicName;
    classvar scalarToDynamicName, scalarValues, toWidth;
    var <command, <direction, <formatHairpinStop, <hide, <leak, <name, <nameIsTextual, ordinal, <sforzando;
    var <tweaks;
    var <context='Voice', <formatSlot='after', <parameter=true;
    *initClass {
        dynamicNames = #[
            'ppppp',
            'pppp',
            'ppp',
            'pp',
            'p',
            'mp',
            'mf',
            'f',
            'ff',
            'fff',
            'ffff',
            'fffff',
            'fp',
            'sf',
            'sff',
            'sp',
            'spp',
            'sfz',
            'sffz',
            'sffp',
            'sffpp',
            'sfp',
            'sfpp',
            'rfz',
            'niente',
            '!'
        ];

        lilypondDynamicCommands = dynamicNames.reject { |elem| elem == 'niente' };

        compositeDynamicNameToSteadyStateDynamicName = (
            'fp': 'p',
            'sf': 'f',
            'sff': 'ff',
            'sfp': 'p',
            'sfpp': 'pp',
            'sffp': 'p',
            'sffpp': 'pp',
            'sffz': 'ff',
            'sfz': 'f',
            'sp': 'p',
            'spp': 'pp',
            'rfz': 'f'
        );

        dynamicNameToDynamicOrdinal = (
            'ppppp': -6,
            'pppp': -5,
            'ppp': -4,
            'pp': -3,
            'p': -2,
            'niente': -inf,
            'mp': -1,
            'mf': 1,
            'f': 2,
            'ff': 3,
            'fff': 4,
            'ffff': 5,
            'fffff': 6
        );

        dynamicNameToScalar = (
            'niente': 0,
            'ppppp': 0.08,
            'pppp': 0.1,
            'ppp': 0.125,
            'pp': 0.25,
            'p': 0.375,
            'mp': 0.5,
            'mf': 0.625,
            'f': 0.75,
            'ff': 0.875,
            'fff': 0.95,
            'ffff': 1,
            'fffff': 1
        );

        scalarValues = dynamicNameToScalar.values.as(Array).sort;

        dynamicOrdinalToDynamicName = (
            -6: 'ppppp',
            -5: 'pppp',
            -4: 'ppp',
            -3: 'pp',
            -2: 'p',
            -1: 'mp',
            // -inf: 'niente',
            1: 'mf',
            2: 'f',
            3: 'ff',
            4: 'fff',
            5: 'ffff',
            6: 'fffff'
        );

        scalarToDynamicName = (
            0: 'niente',
            0.08: 'ppppp',
            0.1: 'pppp',
            0.125: 'ppp',
            0.25: 'pp',
            0.375: 'p',
            0.5: 'mp',
            0.625: 'mf',
            0.75: 'f',
            0.875: 'ff',
            0.95: 'fff',
            1: 'ffff',
            1: 'fffff'
        );

        toWidth = (
            "\"f\"": 2,
            "\"mf\"": 3.5,
            "\"mp\"": 3.5,
            "\"p\"": 2,
            "\"sfz\"": 2.5,
        );
    }
    
    *new { |name, command, direction, formatHairpinStop=false, hide=false, leak=false, nameIsTextual=false,
        ordinal, sforzando=false, tweaks|
        //!!!TODO
        // if name_ == 'niente':
        //     if name_is_textual not in (None, True):
        //         raise Exception('niente dynamic name is always textual.')
        //     name_is_textual = True
        // if not name_is_textual:
        //     for letter in name_.strip('"'):
        //         assert letter in self._lilypond_dynamic_alphabet, repr(name_)
        // self._name = name_
        if (name.isKindOf(FoscDynamic)) { name = name.name };
        if (name.isInteger) { name = this.dynamicOrdinalToDynamicName(name) };
        // if (this.isDynamicName(name).not) {
        //     throw("FoscDynamic: name not recognized: %.".format(name))
        // };
        if (direction.notNil) { assert(#['up', 'down'].includes(direction)) };
        if (ordinal.notNil) { assert(#[inf, -inf].includes(ordinal)) };
        ^super.new.init(name, command, direction, formatHairpinStop, hide, leak, nameIsTextual, ordinal, sforzando, tweaks);
    }
    init { |argName, argCommand, argDirection, argFormatHairpinStop, argHide, argLeak, argNameIsTextual,
        argOrdinal, argSforzando, argTweaks|
        name = argName.asSymbol;
        command = argCommand;
        direction = argDirection; 
        formatHairpinStop = argFormatHairpinStop;
        hide = argHide;
        leak = argLeak;
        nameIsTextual = argNameIsTextual;
        ordinal = argOrdinal;
        sforzando = argSforzando;
        FoscLilypondTweakManager.setTweaks(this, argTweaks);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • command

    Gets explicit command.

    Use to override LilyPond output when a custom dynamic has been defined in an external stylesheet. In the example below, '\sub_f' is a nonstandard LilyPond dynamic. LilyPond will interpret the output above only when the command '\sub_f' is defined somewhere in an external stylesheet.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • context
    
    Gets context. Returns 'Voice'.
    
    a = FoscDynamic('f');
    a.context;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • formatHairpinStop

    Is true when dynamic formats LilyPond ``\!`` hairpin stop.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • hide

    Is true when dynamic should not appear in output (but should still determine effective dynamic).
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • leak

    Is true when dynamic formats LilyPond empty chord ``<>`` symbol.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • name

    Gets name of dynamic.
    
    Returns symbol.

    FoscDynamic('fffff').name;
    FoscDynamic(-6).name;
    FoscDynamic(-inf).name;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • nameIsTextual
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • ordinal

    Gets ordinal value of dynamic.
    
    Returns integer.
    
    FoscDynamic('fffff').ordinal;
    FoscDynamic('fff').ordinal;
    FoscDynamic('p').ordinal;
    FoscDynamic('sffz').ordinal;
    FoscDynamic('niente').ordinal;
    FoscDynamic('foo').ordinal;    // throws exception
    -------------------------------------------------------------------------------------------------------- */
    ordinal {
        if (ordinal.notNil) { ^ordinal };
        name = FoscDynamic.prCoerceDynamicName(name);
        ^FoscDynamic.dynamicNameToDynamicOrdinal(name);
    }
    /* --------------------------------------------------------------------------------------------------------
    • scalar

    Gets scalar value of dynamic.
    
    Returns float.
    
    FoscDynamic('fffff').scalar;
    FoscDynamic('fff').scalar;
    FoscDynamic('p').scalar;
    FoscDynamic('sffz').scalar;
    FoscDynamic('niente').scalar;
    FoscDynamic('foo').scalar;    // throws exception
    -------------------------------------------------------------------------------------------------------- */
    scalar {
        name = FoscDynamic.prCoerceDynamicName(name);
        ^FoscDynamic.dynamicNameToScalar(name);
    }
    /* --------------------------------------------------------------------------------------------------------
    • parameter

    Is true.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • sforzando

    Is true when dynamic name begins in s- and ends in -z
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • ==

    a = FoscDynamic('p');
    b = FoscDynamic('p');
    c = FoscDynamic('f');
    
    a == b;
    a == c;
    -------------------------------------------------------------------------------------------------------- */
    == { |dynamic|
        if (dynamic.isKindOf(this.species).not) { ^false };
        ^(this.name == dynamic.name);
    }
    /* --------------------------------------------------------------------------------------------------------
    • !=

    a = FoscDynamic('p');
    b = FoscDynamic('p');
    c = FoscDynamic('f');
    
    a != b;
    a != c;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString

    FoscDynamic('p').asCompileString;
    -------------------------------------------------------------------------------------------------------- */
    asCompileString {
        ^"FoscDynamic('%')".format(name.asString);
    }
    /* --------------------------------------------------------------------------------------------------------
    • format

    a = FoscDynamic('p');
    a.format;

    a = FoscDynamic('niente');
    a.format;

    a = FoscDynamic('foo');
    a.format;
    -------------------------------------------------------------------------------------------------------- */
    format {
        if (name == 'niente') { ^"" };
        if (FoscDynamic.lilypondDynamicCommands.includes(name).not) {
            throw("%: % is not a Lilypond dynamic command.".format(this.species, name));
        };
        ^this.prGetLilypondFormat;
    }
    /* --------------------------------------------------------------------------------------------------------
    • hash

    !!!TODO: not yet implemented

    a = FoscDynamic('p');
    b = FoscDynamic('p');
    a.hash == b.hash;
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • str

    a = FoscDynamic('p');
    a.str;
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prAddLeak
    -------------------------------------------------------------------------------------------------------- */
    prAddLeak { |string|
        if (leak) { string = "<> %".format(string) };
        ^string;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prAttachmentTestAll
    -------------------------------------------------------------------------------------------------------- */
    prAttachmentTestAll { |componentExpression|
        if (componentExpression.isKindOf(FoscLeaf).not) { ^false };
        ^true;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • dynamicNameToScalar

    Converts name to dynamic ordinal.
    
    Returns integer or negative infinity.
    
    FoscDynamic.dynamicNameToScalar('sfp');
    FoscDynamic.dynamicNameToScalar('ff');
    FoscDynamic.dynamicNameToScalar('niente');
    -------------------------------------------------------------------------------------------------------- */
    *dynamicNameToScalar { |name|
        name = this.prCoerceDynamicName(name);
        ^dynamicNameToScalar[name];
    }
    /* --------------------------------------------------------------------------------------------------------
    • dynamicNameToDynamicOrdinal

    Converts name to dynamic ordinal.
    
    Returns integer or negative infinity.
    
    FoscDynamic.dynamicNameToDynamicOrdinal('sfp');
    FoscDynamic.dynamicNameToDynamicOrdinal('ff');
    FoscDynamic.dynamicNameToDynamicOrdinal('niente');
    -------------------------------------------------------------------------------------------------------- */
    *dynamicNameToDynamicOrdinal { |name|
        name = this.prCoerceDynamicName(name);
        ^dynamicNameToDynamicOrdinal[name];
    }
    /* --------------------------------------------------------------------------------------------------------
    • dynamicOrdinalToDynamicName
    
    Converts ordinal to dynamic name.
    
    Returns symbol.
    
    FoscDynamic.dynamicOrdinalToDynamicName(-5);
    FoscDynamic.dynamicOrdinalToDynamicName(-inf);
    -------------------------------------------------------------------------------------------------------- */
    *dynamicOrdinalToDynamicName { |dynamicOrdinal|
        if (dynamicOrdinal == -inf) { ^'niente' };
        ^dynamicOrdinalToDynamicName[dynamicOrdinal];
    }
    /* --------------------------------------------------------------------------------------------------------
    • scalarToDynamicName
    
    Converts scalar to dynamic name.
    
    Returns symbol.
    
    FoscDynamic.scalarToDynamicName(0.5);
    FoscDynamic.scalarToDynamicName(0);
    -------------------------------------------------------------------------------------------------------- */
    *scalarToDynamicName { |scalar|
        scalar = scalar.nearestInList(scalarValues);
        ^scalarToDynamicName[scalar];
    }
    /* --------------------------------------------------------------------------------------------------------
    • isDynamicName

    Is true when name is a dynamic name. Otherwise false.
    
    Returns true or false.
    
    FoscDynamic.isDynamicName('f');
    FoscDynamic.isDynamicName('niente');
    FoscDynamic.isDynamicName('foo');
    -------------------------------------------------------------------------------------------------------- */
    *isDynamicName { |name|
        ^dynamicNames.includes(name.asSymbol);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prCoerceDynamicName

    FoscDynamic.prCoerceDynamicName('mp');
    FoscDynamic.prCoerceDynamicName('rfz');
    FoscDynamic.prCoerceDynamicName('niente');
    FoscDynamic.prCoerceDynamicName('foo');
    -------------------------------------------------------------------------------------------------------- */
    *prCoerceDynamicName { |name|
        var result;
        name = name.asSymbol;
        assert(this.isDynamicName(name), "FoscDynamic: dynamic name not recognized: '%'.".format(name));
        result = compositeDynamicNameToSteadyStateDynamicName[name];
        if (result.notNil) { ^result } { ^name };
    }
    /* --------------------------------------------------------------------------------------------------------
    • *prTagHide

    !!!TODO
    -------------------------------------------------------------------------------------------------------- */
    *prTagHide { |strings|
        ^this.notYetImplemented(thisMethod);
        // var abjadTags;
        // abjadTags = FoscTags();
        // ^FoscLilypondFormatManager.tag(
        //     strings,
        //     deactivate: false,
        //     tag: 'HIDE_TO_JOIN_BROKEN_SPANNERS'
        // );
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *prFormatTextual

    FoscDynamic.prFormatTextual('meno p');
    -------------------------------------------------------------------------------------------------------- */
    *prFormatTextual { |string, direction|
        string = string.asString;
        if (direction.isNil) { direction = 'down' };
        direction = direction.toTridirectionalLilypondSymbol;
        string = "(markup #:whiteout #:normal-text #:italic \"%\")".format(string);
        string = "% #(make-dynamic-script %)".format(direction, string);
        ^string;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormat
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormat {
        var string;
        case 
        { command.notNil } { string = command }
        { nameIsTextual } { string = FoscDynamic.prFormatTextual(name, direction) }
        { string = ("\\" ++ name.asString) }
        ^string;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormatBundle
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormatBundle { |component|
        var bundle, localTweaks, string;
        bundle = FoscLilypondFormatBundle();
        if (tweaks.notNil) {
            localTweaks = tweaks.prListFormatContributions;
            if (leak) {
                bundle.after.leaks.addAll(localTweaks);
            } {
                bundle.after.articulations.addAll(localTweaks);
            };
        };
        if (hide.not) {
            string = this.prGetLilypondFormat;
            if (leak) {
                bundle.after.leaks.add(string);
            } {
                bundle.after.articulations.add(string);
            };
        };
        ^bundle;
    }
}
