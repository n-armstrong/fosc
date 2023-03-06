/* ------------------------------------------------------------------------------------------------------------
• FoscLilyPondTweakManager (abjad 3.0)

LilyPond tweak manager.


• Example 1

Tweak managers are created by the 'abjad.tweak()' factory function:

>>> beam = abjad.Beam()
>>> abjad.tweak(beam)
LilyPondTweakManager()

------
b = FoscBeam();     
t = tweak(b);           //###### CORRECT
------

Set an attribute like this:

>>> abjad.tweak(beam).color = 'red'

The state of the tweak manager has changed:

>>> abjad.tweak(beam)
LilyPondTweakManager(('color', 'red'))

------
t = tweak(b).color = 'red';
t.cs;                   //###### CLOSE, BUT NOT CORRECT
------

And the value of the attribute just set is available like this:

>>> abjad.tweak(beam).color
'red'

------
tweak(b).color;         //###### NO
------


Trying to get an attribute that has not yet been set raises an
attribute error:

>>> abjad.tweak(beam).foo
Traceback (most recent call last):
    ...
AttributeError: LilyPondTweakManager object has no attribute 'foo'.



• Example X

a = FoscVoice(FoscLeafMaker().(#[60,62,64,65], [1/4]));
a.consistsCommands.add('Horizontal_bracket_engraver');
b = FoscHorizontalBracket();
a[0..].attach(b);
tweak(b).staffPadding = 4;
tweak(b).color = 'red';
b.tweaks.prListFormatContributions;
a.show;
a.format;


• Example Y - TEMPORARY - NOT YET WORKING

a = FoscVoice(FoscLeafMaker().(#[60,62,64,65], [1/4]));
a.consistsCommands.add('Horizontal_bracket_engraver');
b = FoscHorizontalBracketSpanner();
a[0..].attach(b);
// override(b).staffPadding = 4;
c = FoscLilyPondTweakManager();
c.setTweaks(b, #[['color', 'red']]);
a.show;
a.format;
------------------------------------------------------------------------------------------------------------ */
FoscLilyPondTweakManager : FoscLilyPondNameManager {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • at (abjad: __getattr__)
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prAttributeTuples
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prAttributeTuples {
        var result, grob, grobProxy, pairs, attribute, triple;
        result = [];
        vars.keysValuesDo { |name, value|
            if (value.isMemberOf(FoscLilyPondNameManager)) {
                grob = name;
                grobProxy = value;
                pairs.do { |pair|
                    # attribute, value = pair;
                    triple = [grob, attribute, value];
                    result = result.add(triple);
                };
            } {
                result = result.add([name, value]);

            };
        };
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prListFormatContributions


    • Example 1
    
    a = FoscHairpin('p < f');
    m = tweak(a);
    m.color = 'red';
    m.prListFormatContributions;
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prListFormatContributions { |directed=true|
        var result, grob, attribute, value, string;
        result = [];
        this.prAttributeTuples.do { |attributeTuple|
            case
            { attributeTuple.size == 2 } {
                grob = nil;
                # attribute, value = attributeTuple;
            }
            { attributeTuple.size == 3 } {
                # grob, attribute, value = attributeTuple;
            }
            {
                ^throw("%:%: invalid attribute tuple: %."
                    .format(this.species, thisMethod.name, attributeTuple));
            };
            string = FoscLilyPondFormatManager.makeLilypondTweakString(attribute, value, directed, grob);
            result = result.add(string);
        };
        result = result.sort;
        ^result;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • setTweaks

    Sets 'tweaks' on 'object'.

    l = FoscLilyPondLiteral("\\f", 'after', directed: true);
    FoscLilyPondTweakManager.setTweaks(l, #[['color', 'blue']]);
    l.tweaks.prListFormatContributions;

    l = FoscLilyPondLiteral("\\f", 'after', directed: true);
    FoscLilyPondTweakManager.setTweaks(l, #['color', 'blue', size, 12]);
    l.tweaks.prListFormatContributions;    
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    *setTweaks { |object, tweaks|
        var manager, attribute, value, grob;
        if (object.respondsTo('tweaks').not) {
            ^throw("%:% does not allow tweaks (yet).".format(object, thisMethod.name));
        };
        if (tweaks.isNil) {
            ^nil;
        };
        if (tweaks.isKindOf(FoscLilyPondTweakManager)) {
            tweaks = tweaks.prAttributeTuples;
        };
        if (tweaks.isSequenceableCollection.not) {
            ^throw("%:%: tweaks must be an array of tuples (not tweaks!)."
                .format(this.species, thisMethod.name));
        };
        //!!!TODO: deprecate
        // if (object.tweaks.isNil) {
        //     object.prTweaks_(FoscLilyPondTweakManager());
        // };
        //!!!TODO: assert all(isinstance(_, tuple) for _ in tweaks), repr(tweaks)
        if (object.tweaks.isNil) {
            object.instVarPut('tweaks', FoscLilyPondTweakManager());
        };
        manager = object.tweaks;
        if (tweaks.rank == 1) { tweaks = tweaks.clump(2) }; // allow array of key/value pairs
        tweaks.do { |tweak|
            case
            { tweak.size == 2 } {
                # attribute, value = tweak;
                value = value.copy;
                manager[attribute] = value;
            }
            { tweak.size == 3 } {
                # grob, attribute, value = tweak;
                value = value.copy;
                grob = manager[grob];
                grob[attribute] = value;
            }
            {
                ^throw("%:%: tweak tuple must have length 2 or 3: %."
                    .format(this.species, thisMethod.name, tweak));
            };  
        };
    }
}

