/* ------------------------------------------------------------------------------------------------------------
• FoscPlayer

!!!TODO:
• use FoscTag or new FoscPlaybackCommand class for playback commands (see Example 7 below)
• calibrate amplitude mapping for midi and synth playback managers. See example 8 below
    -- amps are comparatively much louder at lower dynamics for synth playback.
• handle 'niente' dynamic trend shapes (o<, >o)



• Example 1

Play a FoscLeaf.

a = FoscChord(#[60,64,67], 1/4);
a.play;


• Example 2

Play a non-simultaneous FoscContainer.

a = FoscStaff(FoscLeafMaker().((60..72), [1/32]));
a[0..].hairpin('ppp < fff');
a.play;


• Example 3

Play a simultaneous FoscContainer.

a = FoscStaff(FoscLeafMaker().((60..72), [1/32]));
b = FoscStaff(FoscLeafMaker().((67..79), [1/32]));
c = FoscScore([a, b]);
c.play;


• Example 4

Play a FoscSelection.

Plays all leaves in order in which they appear in selection.

a = FoscLeafMaker().((60..72), [1/32]);
a.play;


• Example 5

Play a FoscScoreSegment

a = FoscScoreSegment.read(Threads, 'A1');
a.play;


• Example 6

!!!TODO
Play a FoscProject
- will require a 'concat' method for FoscEventSequence


• Example 7

Assign a midi playback manager to a context.

m = FoscMIDIManager(chan: 1);
a = FoscStaff([FoscVoice(FoscLeafMaker().((60..72), [1/32]))], playbackManager: m);
a[0..].hairpin('ppp < fff');
a.play;


• Example 8

Assign a synth playback manager to a context. Defaults to 'default' synthdef.

s.boot;
m = FoscSynthPlaybackManager();
a = FoscStaff([FoscVoice(FoscLeafMaker().((60..72), [1/32]))], playbackManager: m);
a[0..].hairpin('ppp < fff');
a.play;


• Example 9

Assign a synth playback manager to a context. Supply a synthdef to the playback manager.

SynthDef('foo', { |out, freq=440, amp=0.1, nharms=10, gate=1|
    var audio, env;
    audio = Blip.ar(freq, nharms, amp);
    env = Linen.kr(gate, doneAction: 2);
    OffsetOut.ar(out, Pan2.ar(audio, 0, env));
}).add;

m = FoscSynthPlaybackManager(defName: 'foo');
a = FoscStaff([FoscVoice(FoscLeafMaker().((60..72), [1/32]))], playbackManager: m);
a[0..].hairpin('ppp < fff');
a.play;


• Example 10

!!!TODO: rather than use FoscLilypondComment, make an explicit class, e.g. FoscPlaybackCommand
!!! this would mean defining a 'playbackCommand' instance variable in FoscLeaf

Use lilypond comments as arbitrary user-definable commands. This example uses one of the default commands in FoscMIDIPlaybackManager.

a = FoscStaff(FoscLeafMaker().((60..72), [1/16]));
a[0].attach(FoscLilypondComment('sustainOn'));
a[6].attach(FoscLilypondComment('sustainOff'));
a.play;


• Example 11

!!!TODO: rather than use FoscLilypondComment, make an explicit class, e.g. FoscPlaybackCommand
!!! this would mean defining a 'playbackCommand' instance variable in FoscLeaf

Add a user-defined playback command to the playback manager.

m = FoscMIDIPlaybackManager(midiChan: 1);
m.addCommand('foo', { "foo!".postln });
a = FoscStaff(FoscLeafMaker().((60..72), [1/16]), playbackManager: m);
a[6].attach(FoscLilypondComment('foo'));
a.play;


• Example 12

Grace notes are included in playback.

a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
c = FoscGraceContainer([FoscNote(60, 1/8)], slashed: true, slurred: true);
c[0].attach(FoscDynamic('ff'));
a[2].attach(c);
a.play;

a.show;
------------------------------------------------------------------------------------------------------------ */
FoscPlayer : FoscObject {
    var components, eventStreamPlayer;
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    *new { |components|
        ^super.new.init(components);
    }
    init { |argComponents|
        components = argComponents;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • pause
    -------------------------------------------------------------------------------------------------------- */
    pause {
        if (eventStreamPlayer.notNil) { eventStreamPlayer.pause };
    }
    /* --------------------------------------------------------------------------------------------------------
    • play

    a = FoscVoice();
    a.isEmpty;

    a = FoscSelection([]);
    a.isEmpty;
    -------------------------------------------------------------------------------------------------------- */
    play {
        var patterns, recurse, pattern, container;

        // NEW
        if (eventStreamPlayer.notNil) {
            eventStreamPlayer.play;
            ^this;
        };
        
        patterns = [];

        recurse = { |music|
            case
            { music.isKindOf(FoscLeaf) } {
                pattern = FoscPlayer.prGetPattern(music);
                patterns = patterns.add(pattern);
            }
            { music.isKindOf(FoscContainer) } {
                if (music.isSimultaneous) {
                    music.do { |each| recurse.(each) };
                } {
                    if (music.isEmpty.not) {
                        pattern = FoscPlayer.prGetPattern(music);
                        patterns = patterns.add(pattern);
                    };
                };
            }
            { music.isKindOf(FoscSelection) } {
                if (music.isEmpty.not) {
                    container = FoscVoice(music);
                    pattern = FoscPlayer.prGetPattern(container);
                    patterns = patterns.add(pattern);
                    container.prEjectContents;
                };
            }
            { music.isKindOf(FoscScoreSegment) } {
                recurse.(music.score);
            }
            {
                throw("%:new: bad argument for 'music': %.".format(this.species, music));
            };
        };

        Post << "Preparing to play..." << nl;

        recurse.(components);
        eventStreamPlayer = Ppar(patterns);
        eventStreamPlayer.play;
    }
    // play {
    //     var patterns, recurse, pattern, container;
        
    //     patterns = [];

    //     recurse = { |music|
    //         case
    //         { music.isKindOf(FoscLeaf) } {
    //             pattern = FoscPlayer.prGetPattern(music);
    //             patterns = patterns.add(pattern);
    //         }
    //         { music.isKindOf(FoscContainer) } {
    //             if (music.isSimultaneous) {
    //                 music.do { |each| recurse.(each) };
    //             } {
    //                 if (music.isEmpty.not) {
    //                     pattern = FoscPlayer.prGetPattern(music);
    //                     patterns = patterns.add(pattern);
    //                 };
    //             };
    //         }
    //         { music.isKindOf(FoscSelection) } {
    //             if (music.isEmpty.not) {
    //                 container = FoscVoice(music);
    //                 pattern = FoscPlayer.prGetPattern(container);
    //                 patterns = patterns.add(pattern);
    //                 container.prEjectContents;
    //             };
    //         }
    //         {
    //             throw("%:new: bad argument for 'music': %.".format(this.species, music));
    //         };
    //     };

    //     Post << "Preparing to play..." << nl;
    //     recurse.(components);
    //     Post << "Playing!" << nl;
    //     { eventStreamPlayer = Ppar(patterns).play }.defer(0.001);
    // }
    /* --------------------------------------------------------------------------------------------------------
    • resume
    -------------------------------------------------------------------------------------------------------- */
    resume {
        if (eventStreamPlayer.notNil) { eventStreamPlayer.resume };
    }
    /* --------------------------------------------------------------------------------------------------------
    • stop
    -------------------------------------------------------------------------------------------------------- */
    stop {
        if (eventStreamPlayer.notNil) { eventStreamPlayer.stop };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *prGetAmps
    -------------------------------------------------------------------------------------------------------- */
    *prGetAmps { |logicalTies, defaultDynamic|
        var indices, pairs, result, indicators, partSizes, size, nextPair, currentDynamic, triple;
        var startDynamic, dynamicTrend, stopDynamic, startVal, stopVal;

        defaultDynamic = defaultDynamic ?? { FoscDynamic('mf') };
        indices = [];
        pairs = [];
        result = [];
        
        logicalTies.do { |logicalTie, i|
            indicators = logicalTie.head.prGetIndicators([FoscDynamic, FoscDynamicTrend]);
            if (indicators.notEmpty) {
                indices = indices.add(i);
                pairs = pairs.add(indicators);
            };
        };

        partSizes = indices.add(logicalTies.size).intervals;

        pairs.do { |pair, i|
            size = partSizes[i];
            nextPair = pairs[i + 1];

            case 
            { pair.size == 1 } {
                case
                { pair[0].isKindOf(FoscDynamic) } {
                    currentDynamic = pair[0];
                    pair = [currentDynamic, nil];
                }
                { pair[0].isKindOf(FoscDynamicTrend) } {
                    currentDynamic = currentDynamic ? defaultDynamic;
                    pair = [currentDynamic] ++ pair;
                };
            }
            { pair.size == 2 } {
               currentDynamic = pair[0];
            };

            if (nextPair.notNil) {
                case 
                { nextPair[0].isKindOf(FoscDynamic) } {
                    currentDynamic = nextPair[0];
                    triple = pair ++ [currentDynamic];
                }
                { nextPair[0].isKindOf(FoscDynamicTrend) } {
                    triple = pair ++ [currentDynamic];
                };
            } {
                triple = [currentDynamic, nil, currentDynamic];
            };

            # startDynamic, dynamicTrend, stopDynamic = triple;
            startVal = startDynamic.scalar;
            stopVal = stopDynamic.scalar;
            
            //!!!TODO: remove the following hack
            case 
            { stopVal.isNil && startVal.isNil} {
                stopVal = startVal = 0.08;
            }
            { stopVal.isNil && startVal.notNil } {
                stopVal = 0.08;
            }
            { startVal.isNil && stopVal.notNil } {
                startVal = 0.08;
            };
            //!!!TODO
                
            case
            { dynamicTrend.isNil || { dynamicTrend.shape == "--" } } {
                result = result.addAll(Array.fill(size, startVal));
            }
            { dynamicTrend.notNil } {
                result = result.addAll(Array.interpolation(size + 1, startVal, stopVal).drop(-1));
            };
        };

        if (result.isEmpty) { 
            result = Array.fill(logicalTies.size, defaultDynamic.scalar);
        };

        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetPattern
    -------------------------------------------------------------------------------------------------------- */
    *prGetPattern { |music|
        var manager, logicalTies, amps, event, events;

        music.prGetDescendantsStartingWith.reverseDo { |component|
            block { |break|
                if (component.isKindOf(FoscContext) && { component.playbackManager.notNil }) {
                    manager = component.playbackManager;
                    break.value;
                };
            };
        };

        manager = manager ?? { FoscMIDIManager() }; // MIDI as default
        music.prUpdateNow(offsetsInSeconds: true, indicators: true);
        logicalTies = FoscSelection(music).byLogicalTie;
        amps = this.prGetAmps(logicalTies);

        logicalTies.do { |logicalTie, i|
            //!!!TODO: pass graceContainer in to bundle -- see old method below
            event = FoscMIDIBundle(logicalTie, manager, amps[i]).event;
            events = events.add(event);
        };

        //events.printAll; //!!! REMOVE

        ^Pbind(\proto, Pseq(events));
    }
}
