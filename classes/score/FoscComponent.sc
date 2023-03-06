/* ------------------------------------------------------------------------------------------------------------
• FoscComponent
------------------------------------------------------------------------------------------------------------ */
FoscComponent : Fosc {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <indicatorsAreCurrent=false, <isForbiddenToUpdate=false, <overrides, <lilypondSettingNameManager;
    var <measureNumber, <offsetsAreCurrent=false, <offsetsInSecondsAreCurrent=false, <parent, <player; 
    var <startOffset, startOffsetInSeconds, <stopOffset, stopOffsetInSeconds, <timespan, <wrappers;
	*new {
		^super.new.initFoscComponent;
	}
	initFoscComponent {
		timespan = FoscTimespan();
		wrappers = List[];
	}
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • duration
    -------------------------------------------------------------------------------------------------------- */
    duration {
        ^this.prGetDuration;
    }
    /* --------------------------------------------------------------------------------------------------------
    • eventList


    !!!TODO: collect indicators and spanners (e.g. slurs for legato)


    • Example 1

    a = FoscChord([60,64,67], 1/4);
    a.eventList;


    • Example 2

    a = FoscStaff(FoscLeafMaker().((60..72), [1/8]));
    a[0..].hairpin('ppp < fff');
    a.eventList.printAll;


    • Example 3

    Return a list of lists for simultaneous containers.

    a = FoscStaff(FoscLeafMaker().((60..63), [1/32]));
    b = FoscStaff(FoscLeafMaker().((67..70), [1/32]));
    c = FoscScore([a, b]);
    c.eventList; // ^throw exception
    c.eventLists.do { |each| each.printAll; Post.nl };

    c.do { |voice| voice.postln };
    -------------------------------------------------------------------------------------------------------- */
    eventList {
        var events, logicalTies, amps, leaf, dur, midinote, amp, event;

        this.prUpdateNow(offsetsInSeconds: true, indicators: true);

        events = [];
        
        if (this.isKindOf(FoscContainer) && { this.isSimultaneous }) {
            ^throw("'eventList' not implemented for simultaneous containers. Use 'eventLists'.");
        } {
            logicalTies = this.selectLogicalTies;
            amps = this.prGetAmps(logicalTies);

            logicalTies.do { |logicalTie, i|
                leaf = logicalTie.head;
                dur = (logicalTie.tail.stopOffsetInSeconds - leaf.startOffsetInSeconds).asFloat;
                amp = amps[i];
                
                case
                { leaf.isKindOf(FoscNote) } {
                    midinote = leaf.writtenPitch.midinote;
                    //midinote = [midinote];
                }
                { leaf.isKindOf(FoscChord) } {
                    midinote = leaf.writtenPitches.midinotes;
                }
                {
                    midinote = 'rest';
                };

                event = (dur: dur, midinote: midinote, amp: amp);
                events = events.add(event);
            };
        };

        ^events;
    }
    /* --------------------------------------------------------------------------------------------------------
    • pattern
    -------------------------------------------------------------------------------------------------------- */
    pattern {
        ^Pseq(this.eventList);
    }
    /* --------------------------------------------------------------------------------------------------------
    • startOffsetInSeconds

    Gets start offset in seconds. If no effective metronome mark is found, tempo is set to 1/4 = 60.
    -------------------------------------------------------------------------------------------------------- */
    startOffsetInSeconds {
        if (startOffsetInSeconds.notNil) {
            ^startOffsetInSeconds;
        } {
            ^FoscMetronomeMark(#[1,4], 60).durationToSeconds(startOffset);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • stopOffsetInSeconds

    Gets stop offset in seconds. If no effective metronome mark is found, tempo is set to 1/4 = 60.
    -------------------------------------------------------------------------------------------------------- */
    stopOffsetInSeconds {
        if (stopOffsetInSeconds.notNil) {
            ^stopOffsetInSeconds
        } {
            ^FoscMetronomeMark(#[1,4], 60).durationToSeconds(stopOffset);
        };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC INSTANCE METHODS: SPECIAL METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString

    Gets interpreter representation.

    Subclass responsibility.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • copy

    Shallow copies component.

    Copies indicators.

    Does not copy spanners.

    Does not copy children.

    Returns new component.

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/12,1/12,1/12,1/4]));
    b = a.copy;
    b.components;

    • copy with children

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/12,1/12,1/12,1/4]));
    b = a.prCopyWithChildren;
    b.components;
    b.show;

    a.doComponents { |each| each.postln };
    b.doComponents { |each| each.postln }
    -------------------------------------------------------------------------------------------------------- */
    copy {
        var newComponent, manager;
        
        newComponent = this.species.new(*this.storeArgs);
        
        if (this.respondsTo('overrides') && { this.overrides.notNil }) {
            manager = override(this).copy;
            newComponent.instVarPut('overrides', manager);
        };
        
        if (this.respondsTo('lilypondSettingNameManager') && { this.lilypondSettingNameManager.notNil }) {
            manager = set(this).copy;
            newComponent.instVarPut('lilypondSettingNameManager', manager);
        };
        
        FoscInspection(this).annotationWrappers.do { |wrapper|
            newComponent.attach(wrapper.copy);
        };
        
        wrappers.do { |wrapper|
            newComponent.attach(wrapper.copy);
        };
        
        ^newComponent;
    }
    /* --------------------------------------------------------------------------------------------------------
    • illustrate

    Illustrates component.

    Returns LilyPond file.


    • Example 1

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.show;


    • Example 2

    Include a path to custom include files (stylesheets, etc.)

    Fosc.tuning_('et72');
    a = FoscNote("cfts'", 1/4);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    illustrate { |paperSize, staffSize, includes|
        var lincludes, lilypondFile;
        
        lincludes = ["%/default.ily".format(Fosc.stylesheetDirectory)];
        if (Fosc.tuning.notNil) { lincludes = lincludes ++ [Fosc.tuning.stylesheetPath] };
        
        if (includes.notNil) {
            if (includes.isKindOf(SequenceableCollection).not) { includes = [includes] };
            lincludes = lincludes ++ includes;
        };
        
        lilypondFile = FoscLilyPondFile(
            this,
            paperSize: paperSize,
            includes: lincludes,
            staffSize: staffSize
        );

        ^lilypondFile;
    }
	/* --------------------------------------------------------------------------------------------------------
    • storeArgs (abjad: __getnewargs__)
    
    Gets new arguments.

    Returns array.
    -------------------------------------------------------------------------------------------------------- */
    storeArgs {
        ^[];
    }
    /* --------------------------------------------------------------------------------------------------------
    • str
    -------------------------------------------------------------------------------------------------------- */
    str {
        ^this.format;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: TOP LEVEL
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • annotate

    a = FoscNote(60, 1/4);
    a.annotate('foo', FoscClef('bass'));
    FoscInspection(a).annotation('foo');
    -------------------------------------------------------------------------------------------------------- */
    annotate { |annotation, indicator|
        var wrapper;
        //!!!TODO: assert(annotation.isKindOf(Symbol)); // String as well ??
        // receiver must be a subclass of FoscComponent !!! -- move this method to FoscComponent ?
        wrapper = FoscWrapper(
            component: this,
            indicator: indicator,
            annotation: annotation
        );
    }
    /* --------------------------------------------------------------------------------------------------------
    • attach

    !!! TODO: attaches to FoscComponents and FoscSelections? can this method be moved lower in the tree?
    !!! if not, then add a test for a valid receiver type to the top of the method.

    Attaches 'attachment' to receiver.

    First form attaches indicator to single leaf.

    Second form attaches spanner to leaf selection.

    Third for attaches grace container to leaf.

    Fourth form attaches time signature to measure.

    Fifth form attaches wrapper to unknown (?).

    Returns nil.


    • Example 1

    Attaches clef to first note in staff:

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a[0].attach(FoscClef('bass'));
    a[0].wrappers;
    a.format;


    • Example 2

    Attaches accent to last note in staff:

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a[3].attach(FoscArticulation('>'));
    a.format;
    -------------------------------------------------------------------------------------------------------- */
    attach { |attachment, context, deactivate, syntheticOffset, wrapper=false|
        var target, nonIndicatorPrototype, result, message, graceContainer, isAcceptable, component;
        var annotation, indicator, localWrapper;

        target = this;
        assert(attachment.notNil, "%:attach: attachment cannot be nil.".format(target));
        nonIndicatorPrototype = [FoscAfterGraceContainer, FoscGraceContainer];

        if (context.notNil && nonIndicatorPrototype.any { |type| attachment.isKindOf(type) }) {
            ^throw("%:attach: set context for indicators, not %.".format(target, attachment));
        };

        if (attachment.respondsTo('prBeforeAttach')) { attachment.prBeforeAttach(target) };

        if (attachment.respondsTo('prAttachmentTestAll')) {
            result = attachment.prAttachmentTestAll(target);
            if (result.not) {
                assert(result.isSequenceableCollection);
                result = result.collect { |each| " " ++ each };
                message = "%.prAttachmentTestAll():";
                result.insert(0, message);
                message = result.join("\n");
                ^throw(message);
            };
        };

        graceContainer = [FoscAfterGraceContainer, FoscGraceContainer];

        case
        { graceContainer.any { |type| attachment.isKindOf(type) } } {
            if (target.isKindOf(FoscLeaf).not) {
                ^throw("%:attach: grace containers attach to a single leaf.".format(target));
            };
            attachment.prAttach(target);
            ^target;
        };

        assert(target.isKindOf(FoscComponent));

        case
        { target.isKindOf(FoscContainer) } {
            isAcceptable = false;
            if ([Dictionary, String, FoscWrapper].any { |type| attachment.isKindOf(type) }) {
                isAcceptable = true;
            };
            if (attachment.respondsTo('canAttachToContainers') &&
                { attachment.canAttachToContainers == true }) {
                isAcceptable = true;
            };
            if (isAcceptable.not) {
                ^throw("Can't attach % to a container: %.".format(attachment, target));
            };
        }
        { target.isKindOf(FoscLeaf).not } {
            ^throw("%:attach: indicator % must attach to leaf, not: %.".format(target, attachment, target));
        };

        component = target;
        assert(component.isKindOf(FoscComponent));

        annotation = nil;

        if (attachment.isKindOf(FoscWrapper)) {
            annotation = attachment.annotation;
            context = context ?? { attachment.context };
            deactivate = deactivate ?? { attachment.deactivate };
            syntheticOffset = syntheticOffset ?? { attachment.syntheticOffset };
            attachment.prDetach;
            attachment = attachment.indicator;
        };

        if (attachment.respondsTo('context')) {
            context = context ?? { attachment.context };
        };

        localWrapper = FoscWrapper(
            annotation: annotation,
            component: component,
            context: context,
            deactivate: deactivate ? false,
            indicator: attachment,
            syntheticOffset: syntheticOffset
        );

        //localWrapper.prBindComponent(component);
        if (wrapper) { ^localWrapper };
    }
    /* --------------------------------------------------------------------------------------------------------
    • detach

    • Example 1

    Detach by class.

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a[0].attach(FoscArticulation('>'));
    a.format;

    a[0].detach(FoscArticulation);
    a.format;


    • Example 2

    Detach by instance.

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    i = FoscArticulation('>');
    a[0].attach(i);
    a.format;

    a[0].detach(i);
    a.format;
    -------------------------------------------------------------------------------------------------------- */
    detach { |object, byID=false|
        var target, afterGraceContainer, graceContainer, inspector, result;

        target = this;
        inspector = FoscInspection(target);

        if (object.isKindOf(Class)) {
            case
            { object === FoscAfterGraceContainer } {
                afterGraceContainer = inspector.afterGraceContainer;
            }
            { object === FoscGraceContainer } {
                graceContainer = inspector.graceContainer;
            }
            {
                assert(target.respondsTo('wrappers'));
                result = [];
                
                target.wrappers.do { |wrapper|
                    if (wrapper.isKindOf(object)) {
                        target.wrappers.remove(wrapper);
                        result = result.add(wrapper);
                    } {
                        if (wrapper.indicator.isKindOf(object)) {
                            wrapper.prDetach;
                            result = result.add(wrapper.indicator);
                        };
                    };
                };

                ^result;
            };
        } {
            case
            { object.isKindOf(FoscAfterGraceContainer) } {
                afterGraceContainer = inspector.afterGraceContainer;
            }
            { object.isKindOf(FoscGraceContainer) } {
                graceContainer = inspector.graceContainer;
            }
            {
                assert(target.respondsTo('wrappers'));
                result = [];
                
                target.wrappers.do { |wrapper|
                    if (wrapper === object) {
                        wrapper.prDetach;
                        result = result.add(wrapper);
                    } {
                        if (wrapper.indicator == object) {
                            if (byID && { object.hash != wrapper.indicator.hash }) {
                                // pass
                            } {
                                wrapper.prDetach;
                                result = result.add(wrapper.indicator);
                            };
                        };
                    };
                };

                ^result;
            };
        };

        result = [];
        if (afterGraceContainer.notNil) { result = result.add(afterGraceContainer) };
        if (graceContainer.notNil) { result = result.add(graceContainer) };
        
        if (byID) {
            result = result.select { |each| each.hash == object.hash };
        };
        
        result.do { |each| each.prDetach };
        ^result;

    }
    /* --------------------------------------------------------------------------------------------------------
    • override


    • Example 1

    a = FoscNote(60, 1/4);
    override(a).noteHead.color = 'red';
    override(a).noteHead.fontSize = 4;
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    override {
        if (this.respondsTo('overrides').not) {
            ^throw("%: does not respond to override.".format(this.species));
        };

        if (this.overrides.isNil) {
            this.instVarPut('overrides', FoscLilyPondGrobNameManager());
        };

        ^this.overrides;
    }
    /* --------------------------------------------------------------------------------------------------------
    • set

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], 1/8));
    set(a).instrumentName = FoscMarkup("Violin");
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    set {
        if (this.respondsTo('lilypondSettingNameManager').not) {
            ^throw("%: does not respond to set.".format(this.species));
        };

        if (this.lilypondSettingNameManager.isNil) {
            this.instVarPut('lilypondSettingNameManager', FoscLilyPondSettingNameManager());
        };

        ^this.lilypondSettingNameManager;
    }
    /* --------------------------------------------------------------------------------------------------------
    • tweak

    • Example 1

    Tweaks markup:

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    m = FoscMarkup('Allegro assai', direction: 'above');
    tweak(m).color = 'red';
    m.format;
    a[0].attach(m);
    a.format;


    Survives copy:

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    m = FoscMarkup('Allegro assai', direction: 'above');
    tweak(m).color = 'red';
    n = m.copy;
    a.leafAt(0).attach(n);
    //a.show;
    a.format;


    !!!TODO: DOES NOT SURVIVE DOT-CHAINING

    Survives dot-chaining:

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    m = FoscMarkup('Allegro assai', direction: 'above');
    tweak(m).color = 'red';
    m = m.italic;
    a.leafAt(0).attach(m);
    //a.show;
    a.format;

    ### abjad:
    \new Staff
    {
        c'4
        - \tweak color #red
        ^ \markup {
            \italic
                "Allegro assai"
            }
        d'4
        e'4
        f'4
    }
    ###

    !!!TODO: DOES NOT WORK FOR OPPOSITE-DIRECTED COINCIDENT MARKUP

    Works for opposite-directed coincident markup:

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    m = FoscMarkup('Allegro assai', direction: 'above');
    tweak(m).color = 'red';
    a.leafAt(0).attach(m);
    n = FoscMarkup('... ma non troppo', direction: 'below');
    tweak(n).color = 'blue';
    tweak(n).staffPadding = 4;
    a.leafAt(0).attach(n);
    //a.show;
    a.format;

    ### abjad:
    \new Staff
    {
        c'4
        - \tweak color #red
        ^ \markup { "Allegro assai ..." }
        - \tweak color #blue
        - \tweak staff-padding #4
        _ \markup { "... ma non troppo" }
        d'4
        e'4
        f'4
    }
    ###


    !!!TODO: NOT WORKING

    Ignored for same-directed coincident markup:

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    m = FoscMarkup('Allegro assai', direction: 'above');
    tweak(m).color = 'red';
    a.leafAt(0).attach(m);
    n = FoscMarkup('... ma non troppo', direction: 'above');
    tweak(n).color = 'blue';
    tweak(n).staffPadding = 4;
    a.leafAt(0).attach(n);
    //a.show;
    a.format;

    ### abjad:
    \new Staff
    {
        c'4
        - \tweak color #red
        ^ \markup { "Allegro assai ..." }
        - \tweak color #blue
        - \tweak staff-padding #4
        ^ \markup { "... ma non troppo" }
        d'4
        e'4
        f'4
    }
    ###


    • Example 2


    Tweaks note-head:

    a = FoscStaff(FoscLeafMaker().(#[60,61,62,63], [1/4]));
    tweak(a[1].noteHead).color = 'red';
    a.format;


    !!!TODO: NOT YET IMPLEMENTED

    Tweaks grob aggregated to note-head:

    a = FoscStaff(FoscLeafMaker().(#[60,61,62,63], [1/4]));
    tweak(a[1].noteHead).accidental.color = 'red';
    //a.show;
    a.format;

    ### abjad:
    \new Staff
    {
        c'4
        \tweak Accidental.color #red
        cs'4
        d'4
        ds'4
    }
    ###


    • Example 3

    Returns LilyPond tweak manager:

    m = FoscMarkup('Allegro assai', direction: 'above');
    tweak(m);


    • Example 4

    !!!TODO:

    Tweak objectessions work like this:

    >>> abjad.tweak('red').color
    LilyPondTweakManager(('color', 'red'))

    >>> abjad.tweak(6).Y_offset
    LilyPondTweakManager(('Y_offset', 6))

    >>> abjad.tweak(False).bound_details__left_broken__text
    LilyPondTweakManager(('bound_details__left_broken__text', False))



    • Example X

    a = FoscHairpin('p < f');
    b = FoscLilyPondTweakManager();
    b.setTweaks(a, #[['dynamicLineSpanner', 5]]);
    a.tweaks;
    a.tweaks.prGetAttributePairs;


    • Example Y

    a = FoscHorizontalBracket();
    tweak(a).color = 'red';
    tweak(a).staffPadding = 5;
    a.tweaks.prGetAttributePairs;
    -------------------------------------------------------------------------------------------------------- */
    tweak {
        var constants, type, manager;
        
        if (this.respondsTo('tweaks').not) {
            ^throw("% does not respond to tweak.".format(this.species));
        };
        constants = #['above', 'below', 'down', 'left', 'right', 'up'];
        type = [Boolean, SimpleNumber, String, SequenceableCollection, FoscScheme];
        
        if (constants.includes(this) || { type.any { |type| this.isKindOf(type) } }) {
            manager = FoscLilyPondTweakManager();
            manager.pendingValue_(this);
            ^manager;
        };
        
        if (this.tweaks.isNil) {
            this.instVarPut('tweaks', FoscLilyPondTweakManager());
        };
        
        ^this.tweaks;
    }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prGetAmps
    -------------------------------------------------------------------------------------------------------- */
    prGetAmps { |logicalTies, defaultDynamic|
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
            startVal = startDynamic.amp;
            stopVal = stopDynamic.amp;
            
            //!!!TODO: hack - remove
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
                
            case
            { dynamicTrend.isNil || { dynamicTrend.shape == "--" } } {
                result = result.addAll(Array.fill(size, startVal));
            }
            { dynamicTrend.notNil } {
                result = result.addAll(Array.interpolation(size + 1, startVal, stopVal).drop(-1));
            };
        };

        if (result.isEmpty) { 
            result = Array.fill(logicalTies.size, defaultDynamic.amp);
        };

        ^result;
    }
    // prGetAmps { |logicalTies, defaultDynamic|
    //     var indices, pairs, result, indicators, partSizes, size, nextPair, currentDynamic, triple;
    //     var startDynamic, dynamicTrend, stopDynamic, startVal, stopVal;

    //     defaultDynamic = defaultDynamic ?? { FoscDynamic('mf') };
    //     indices = [];
    //     pairs = [];
    //     result = [];
        
    //     logicalTies.do { |logicalTie, i|
    //         indicators = logicalTie.head.prGetIndicators([FoscDynamic, FoscDynamicTrend]);
    //         if (indicators.notEmpty) {
    //             indices = indices.add(i);
    //             pairs = pairs.add(indicators);
    //         };
    //     };

    //     partSizes = indices.add(logicalTies.size).intervals;

    //     pairs.do { |pair, i|
    //         size = partSizes[i];
    //         nextPair = pairs[i + 1];

    //         case 
    //         { pair.size == 1 } {
    //             case
    //             { pair[0].isKindOf(FoscDynamic) } {
    //                 currentDynamic = pair[0];
    //                 pair = [currentDynamic, nil];
    //             }
    //             { pair[0].isKindOf(FoscDynamicTrend) } {
    //                 currentDynamic = currentDynamic ? defaultDynamic;
    //                 pair = [currentDynamic] ++ pair;
    //             };
    //         }
    //         { pair.size == 2 } {
    //            currentDynamic = pair[0];
    //         };

    //         if (nextPair.notNil) {
    //             case 
    //             { nextPair[0].isKindOf(FoscDynamic) } {
    //                 currentDynamic = nextPair[0];
    //                 triple = pair ++ [currentDynamic];
    //             }
    //             { nextPair[0].isKindOf(FoscDynamicTrend) } {
    //                 triple = pair ++ [currentDynamic];
    //             };
    //         } {
    //             triple = [currentDynamic, nil, currentDynamic];
    //         };

    //         # startDynamic, dynamicTrend, stopDynamic = triple;
    //         startVal = startDynamic.scalar;
    //         stopVal = stopDynamic.scalar;
            
    //         //!!!TODO: hack - remove
    //         case 
    //         { stopVal.isNil && startVal.isNil} {
    //             stopVal = startVal = 0.08;
    //         }
    //         { stopVal.isNil && startVal.notNil } {
    //             stopVal = 0.08;
    //         }
    //         { startVal.isNil && stopVal.notNil } {
    //             startVal = 0.08;
    //         };
                
    //         case
    //         { dynamicTrend.isNil || { dynamicTrend.shape == "--" } } {
    //             result = result.addAll(Array.fill(size, startVal));
    //         }
    //         { dynamicTrend.notNil } {
    //             result = result.addAll(Array.interpolation(size + 1, startVal, stopVal).drop(-1));
    //         };
    //     };

    //     if (result.isEmpty) { 
    //         result = Array.fill(logicalTies.size, defaultDynamic.scalar);
    //     };

    //     ^result;
    // }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PRIVATE INSTANCE METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prCacheNamedChildren
    -------------------------------------------------------------------------------------------------------- */
    prCacheNamedChildren {
        var nameDictionary;
        
        nameDictionary = ();
        
        if (this.respondsTo('namedChildren') && { this.namedChildren.notNil }) {
            this.namedChildren.keysValuesDo { |name, children|
                nameDictionary[name] = children.copy;
            };
        };
        
        if (this.respondsTo('name') && { this.name.notNil }) {
            if (nameDictionary.keys.includes(this.name).not) { nameDictionary[this.name] = [] };
            nameDictionary[this.name] = nameDictionary[this.name].add(this);
        };
        
        ^nameDictionary;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prCheckForCycles

    a = FoscVoice([FoscNote(60, 1/4)]);
    a.add(FoscNote(67, 3/4));
    a.format
    -------------------------------------------------------------------------------------------------------- */
    prCheckForCycles { |components|
        var parentage;
        parentage = this.prGetParentage;
        components.do { |component| if (parentage.includes(component)) { ^true } };
        ^false;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prExtract

    see FoscMutation: extract for examples
    -------------------------------------------------------------------------------------------------------- */
    prExtract { |scaleContents=false|
        var selection, parent, start, stop, components;
        
        if (scaleContents) { this.prScaleContents(this.multiplier) };
        selection = FoscSelection([this]);
        # parent, start, stop = selection.prGetParentAndStartStopIndices;
        
        if (parent.isNil) {
            ^throw("%:%: can't extract a component without a parent.".format(this.species, thisMethod.name));
        };
        
        components = if (this.respondsTo('components')) { this.components } { [] };
        parent.prSetItem((start..(stop + 1)), components);
        
        ^this;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatAbsoluteAfterSlot
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prFormatAbsoluteAfterSlot { |bundle|
        var result;
        result = result.add(['literals', bundle.absoluteAfter.commands]);
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatAbsoluteBeforeSlot
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prFormatAbsoluteBeforeSlot { |bundle|
        var result;
        result = result.add(['literals', bundle.absoluteBefore.commands]);
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatAfterSlot
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prFormatAfterSlot { |bundle|
        // pass
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatBeforeSlot
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prFormatBeforeSlot { |bundle|
        // pass
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatCloseBracketsSlot
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prFormatCloseBracketsSlot { |bundle|
        // pass
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatClosingSlot
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prFormatClosingSlot { |bundle|
        // pass
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatComponent

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.prGetFormatPieces;

    a = FoscVoice(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.prGetFormatPieces;

    a = FoscTuplet(2/3, [FoscNote(60, 2/4), FoscNote(60, 1/4)]);
    a.prGetFormatPieces(true);
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    prFormatComponent { |pieces=false|
        var result, contributions, bundle, contributor, contribution;
        
        result = [];
        contributions = [];
        bundle = FoscLilyPondFormatManager.bundleFormatContributions(this);
        
        result = result.addAll(this.prFormatAbsoluteBeforeSlot(bundle));
        result = result.addAll(this.prFormatBeforeSlot(bundle));
        result = result.addAll(this.prFormatOpenBracketsSlot(bundle));
        result = result.addAll(this.prFormatOpeningSlot(bundle));
        result = result.addAll(this.prFormatContentsSlot(bundle));
        result = result.addAll(this.prFormatClosingSlot(bundle));
        result = result.addAll(this.prFormatCloseBracketsSlot(bundle));
        result = result.addAll(this.prFormatAfterSlot(bundle));
        result = result.addAll(this.prFormatAbsoluteAfterSlot(bundle));
        
        result.do { |each|
            # contributor, contribution = if (each.size == 1) { each[0] } { each };
            contributions = contributions.addAll(contribution) 
        };
        
        if (pieces) { ^contributions } { ^contributions.join("\n") };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatContributionsForSlot
    -------------------------------------------------------------------------------------------------------- */
    // DEPRECATED
    // prFormatContributionsForSlot { |slotIdentifier, bundle|
    //     var result, manager, slotNames, slotIndex, slotName, methodName, method;
    //     result = [];
    //     if (bundle.isNil) {
    //         manager = FoscLilyPondFormatManager;
    //         bundle = manager.bundleFormatContributions(this);
    //     };
    //     slotNames = #[
    //         'before',
    //         'openBrackets',
    //         'opening',
    //         'contents',
    //         'closing',
    //         'closeBrackets',
    //         'after'
    //     ];
    //     case
    //     { slotIdentifier.isInteger } {
    //         if (slotIdentifier.inclusivelyBetween(1, 7).not) {
    //             ^throw("%:%: slotIdentifier must be between 1 and 7.".format(this.species, thisMethod.name));
    //         };
    //         slotIndex = slotIdentifier - 1;
    //         slotName = slotNames[slotIndex];
    //     }
    //     { [String, Symbol].any { |type| slotIdentifier.isKindOf(type) } } {
    //         if (slotNames.includes(slotName).not) {
    //             ^throw("%:% % is not in slotNames.".format(this.species, thisMethod.name, slotName));
    //         };
    //     };
    //     methodName = "prFormat%Slot".format(slotName.asString.capitalizeFirst);
    //     methodName = methodName.asSymbol;
    //     this.perform(methodName, bundle).do { |each|
    //         result = result ++ each[0];
    //     };
    //     ^result;
    // }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatContentsSlot
    -------------------------------------------------------------------------------------------------------- */
    prFormatContentsSlot { |bundle|
        // pass
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatOpenBracketsSlot
    -------------------------------------------------------------------------------------------------------- */
    prFormatOpenBracketsSlot { |bundle|
        // pass
    }
    /* --------------------------------------------------------------------------------------------------------
    • prFormatOpeningSlot
    -------------------------------------------------------------------------------------------------------- */
    prFormatOpeningSlot { |bundle|
        // pass
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetContents
    -------------------------------------------------------------------------------------------------------- */
    prGetContents {
        var result;
        
        result = [this];
        
        if (this.respondsTo('components') && { this.components.notNil }) {
            result = result.addAll(this.components);
        };
        
        result = FoscSelection(result);
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetDescendantsStartingWith
    -------------------------------------------------------------------------------------------------------- */
    prGetDescendantsStartingWith {
        var result;
        
        result = [this];
        
        if (this.isKindOf(FoscContainer)) {
            if (this.isSimultaneous) {
                this.do { |each| result = result ++ each.prGetDescendantsStartingWith };
            } {
                result = result ++ this[0].prGetDescendantsStartingWith;
            };
        };
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetDescendantsStoppingWith
    -------------------------------------------------------------------------------------------------------- */
    prGetDescendantsStoppingWith {
        var result;
        
        result = [this];
        
        if (this.isKindOf(FoscContainer)) {
            if (this.isSimultaneous) {
                this.do { |each| result = result ++ each.prGetDescendantsStoppingWith };
            } {
                result = result ++ this[this.size - 1].prGetDescendantsStoppingWith;
            };
        };
        
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetDuration
 
    a = FoscNote(60, 1/4);
    a.prGetDuration.str;
    a.prGetDuration(inSeconds: true).asFloat; // defaults to mm. 1/4 = 60

    a = FoscStaff(FoscLeafMaker().(60 ! 7, [1/4]));
    a.prGetDuration.str;

    a = FoscTuplet(2/3, [FoscNote(60, 1/4)]);
    a[0].prGetDuration.str;
    a[0].prGetDuration(inSeconds: true).asFloat; // defaults to mm. 1/4 = 60
    -------------------------------------------------------------------------------------------------------- */
    prGetDuration { |inSeconds=false|
        case
        { inSeconds } {
            ^this.prGetDurationInSeconds;
        }
        { parent.isNil } {
            ^this.prGetPreprolatedDuration;
        }
        {
            ^(parent.prGetParentage.prolation * this.prGetPreprolatedDuration);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetEffective

    • dynamics persist

    a = FoscVoice(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a[0].attach(FoscDynamic('p'));
    a[2].attach(FoscDynamic('f'));
    a.doLeaves { |leaf| leaf.prGetEffective(FoscDynamic).cs.postln };

    • metronome marks persist
    
    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    b = FoscScore([a]);
    b.leafAt(0).attach(FoscMetronomeMark(#[1,4], 60));
    b.leafAt(2).attach(FoscMetronomeMark(#[1,4], 90));
    a.doLeaves { |leaf| leaf.prGetEffective(FoscMetronomeMark).unitsPerMinute.postln };
    -------------------------------------------------------------------------------------------------------- */
    prGetEffective { |type, attributes, command, n=0, unwrap=true|
        var candidateWrappers, parentage, enclosingVoiceName, localWrappers, appendWrapper=false, offset;
        var allOffsets, index, wrapper;

        this.prUpdateNow(indicators: true);
        candidateWrappers = ();
        parentage = this.prGetParentage(graceNotes: true);

        parentage.do { |component|
            if (component.isKindOf(FoscVoice)) {
                if (enclosingVoiceName.notNil && { component.name != enclosingVoiceName }) {
                    // continue
                } {
                    enclosingVoiceName = component.name ?? { component.hash };
                };
            };
            localWrappers = [];

            component.wrappers.do { |wrapper|          
                case { wrapper.annotation.notNil } {
                    // continue
                }
                { wrapper.indicator.isKindOf(type) } {
                    appendWrapper = true;
                    if (command.notNil && { wrapper.indicator.command != command }) {
                        // contimue
                    } {
                        if (attributes.notNil) {
                            attributes.keysValuesDo { |name, val|
                                if (wrapper.indicator.respondsTo('name') && { wrapper.name != val }) {
                                    appendWrapper = false;
                                };
                            };
                        };
                        if (appendWrapper) { localWrappers = localWrappers.add(wrapper) };
                    };
                };
            };

            if (
                localWrappers.any { |wrapper| wrapper.deactivate }
                && { localWrappers.all { |wrapper| wrapper.deactivate }.not }
            ) {
                localWrappers = localWrappers.select { |each| each.deactivate.not };
            };

            localWrappers.do { |wrapper|
                offset = wrapper.startOffset;
                if (candidateWrappers[offset].isNil) { candidateWrappers[offset] = List[] };
                candidateWrappers[offset].add(wrapper);
            };

            case
            { component.isKindOf(FoscContext).not } {
                // continue
            } {
                component.dependentWrappers.do { |wrapper|
                    case 
                    { wrapper.annotation.notNil } {
                        // continue
                    } {
                        if (wrapper.indicator.isKindOf(type)) {
                            appendWrapper = true;
                            case { command.notNil && { wrapper.indicator.command != command } } {
                                // continue
                            } {
                                if (attributes.notNil) {
                                    attributes.keysValuesDo { |name, val|
                                        if (
                                            wrapper.indicator.respondsTo('name')
                                            && { wrapper.name != val })
                                        {
                                            appendWrapper = false;
                                        };
                                    };
                                };
                                if (appendWrapper) {
                                    offset = wrapper.startOffset;
                                    if (candidateWrappers[offset].isNil) { 
                                        candidateWrappers[offset] = List[];
                                    };
                                    candidateWrappers[offset].add(wrapper);
                                };
                            };
                        };
                    };
                };
            };
        };

        if (candidateWrappers.isEmpty) { ^nil };

        allOffsets = candidateWrappers.keys.as(Array).sort;
        startOffset = this.prGetTimespan.startOffset;
        index = allOffsets.bisect(startOffset) - 1 + (n.asInteger);
        
        if (index < 0) {
            ^nil;
        } {
            if (candidateWrappers.size <= index) { ^nil };
        };
        
        wrapper = candidateWrappers[allOffsets[index]][0];
        if (unwrap) { ^wrapper.indicator };
        
        ^wrapper;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetEffectiveStaff

    a = FoscStaff({ |i| FoscNote(60 + i, [1, 4]) } ! 4);
    a[0].prGetEffectiveStaff == a;
    -------------------------------------------------------------------------------------------------------- */
    prGetEffectiveStaff {
        var staffChange, effectiveStaff;
        
        staffChange = this.prGetEffective(FoscStaffChange);
        
        if (staffChange.notNil) {
            effectiveStaff = staffChange.staff;
        } {
            effectiveStaff = this.prGetParentage.firstInstanceOf(FoscStaff);
        };
        
        ^effectiveStaff;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetFormatPieces
    
    a = FoscContainer([FoscNote(60, [1, 4])]);
    a.prGetFormatPieces;

    a = FoscContainer([FoscNote(60, [1, 4])]);
    a.prFormatComponent(true);

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.prGetFormatPieces;

    a = FoscVoice(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    a.prGetFormatPieces;

    a = FoscTuplet(2/3, [FoscNote(60, 2/4), FoscNote(60, 1/4)]);
    a.prFormatComponent(true);
    -------------------------------------------------------------------------------------------------------- */
    prGetFormatPieces {
        var result;
        result = this.prFormatComponent(pieces: true);
        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetIndicator
    
    x = FoscNote(60, 1/4);
    x.attach(FoscDynamic('p'));
    x.prGetIndicator.cs;

    x.attach(FoscArticulation('>'));
    x.prGetIndicator;

    x.prGetIndicator(FoscDynamic).cs;

    x.prGetIndicator(FoscMetronomeMark);
    -------------------------------------------------------------------------------------------------------- */
    prGetIndicator { |type, attributes, unwrap=true|
        var indicators;
        
        indicators = this.prGetIndicators(type, attributes, unwrap);
        
        case 
        { indicators.isEmpty } {
            ^nil;
            // ^throw("%:% no attached indicators found matching %."
            //     .format(this.species, thisMethod.name,type));
        }
        { indicators.size > 1 } {
            ^throw("%:% multiple attached indicators found matching %."
                .format(this.species, thisMethod.name,type));
        }
        { ^indicators[0] };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetIndicators

    x = FoscNote(60, 1/4);
    x.attach(FoscDynamic('p'));
    x.attach(FoscArticulation('>'));
    x.prGetIndicators;
    -------------------------------------------------------------------------------------------------------- */
    prGetIndicators { |type, attributes, unwrap=true|
        var result, prototypeObjects, prototypeClasses, indicator, newResult;

        result = [];
        type = type ? [Object];
        if (type.isSequenceableCollection.not) { type = [type] };
        prototypeObjects = [];
        prototypeClasses = [];
        
        type.do { |indicatorPrototype|
            if (type.any { |type| indicatorPrototype.isKindOf(Class) }) {
                prototypeClasses = prototypeClasses.add(indicatorPrototype);
            } {
                prototypeObjects = prototypeObjects.add(indicatorPrototype);
            };
        };

        wrappers.do { |wrapper|
            if (wrapper.annotation.isNil) {
                case
                { prototypeClasses.any { |type| wrapper.isKindOf(type) } } {
                    result = result.add(wrapper);
                }
                { prototypeObjects.any { |each| wrapper == each } } {
                    result = result.add(wrapper);
                }
                { wrapper.isKindOf(FoscWrapper) } {
                    if (prototypeClasses.any { |type| wrapper.indicator.isKindOf(type) }) {
                        result = result.add(wrapper);
                    } {
                        if (prototypeObjects.any { |each| wrapper.indicator == each }) {
                            result = result.add(wrapper);
                        };
                    };
                };
            };
        };

        if (attributes.notNil) {
            newResult = [];
            
            result.do { |wrapper|
                attributes.vars.keysValuesDo { |key, val|
                    block { |break|
                        if (wrapper.indicator.respondsTo('key') && { wrapper.indicator.key != val }) {
                            break.value;
                        };
                    };
                };

                newResult = newResult.add(wrapper);
            };
            
            result = newResult;
        };

        if (unwrap) { result = result.collect { |each| each.indicator } };

        ^result;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetLilypondFormat

    a = FoscNote(60, 1/4);
    a.prGetLilypondFormat;

    a = FoscChord(#[60,64,67,72], 1/4);
    a.prGetLilypondFormat;
    -------------------------------------------------------------------------------------------------------- */
    prGetLilypondFormat {
        this.prUpdateNow(indicators: true);
        ^this.prFormatComponent;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetMarkup

    a = FoscNote(60, 1/4);
    a.attach(FoscMarkup("foo"));
    a.prGetMarkup;
    -------------------------------------------------------------------------------------------------------- */
    prGetMarkup { |direction|
        var markup;
        
        markup = this.prGetIndicators(FoscMarkup);
        
        case
        { direction == 'up' } { ^markup.select { |each| each.direction == 'up' } }
        { direction == 'down' } { ^markup.select { |each| each.direction == 'down' } };
        
        ^markup;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetParentage
    
    a = FoscTuplet(2/3, [FoscNote(60, 1/4)]);
    a[0].prGetParentage.components;
    -------------------------------------------------------------------------------------------------------- */
    prGetParentage { |graceNotes=false|
        ^FoscParentage(this, graceNotes);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prGetTimespan

    a = FoscNote(60, 1/4);
    b = a.prGetTimespan;
    [b.startOffset.cs, b.stopOffset.cs].postln;
    -------------------------------------------------------------------------------------------------------- */
    prGetTimespan { |inSeconds=false|
        if (inSeconds) {
            this.prUpdateNow(offsetsInSeconds: true);
            
            if (startOffsetInSeconds.isNil) {
                ^throw("%:%: missing metronome mark.".format(this.species, thisMethod.name));
            };
            
            ^FoscTimespan(startOffsetInSeconds, stopOffsetInSeconds);
        } {
            this.prUpdateNow(offsets: true);
            ^timespan;
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prHasEffectiveIndicator

    x = FoscNote(60, 1/4);
    x.attach(FoscDynamic('p'));
    x.prHasEffectiveIndicator(FoscDynamic);
    -------------------------------------------------------------------------------------------------------- */
    prHasEffectiveIndicator { |type, attributes, command|
        var indicator;
        indicator = this.prGetEffective(type, attributes, command);
        ^indicator.notNil;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prHasIndicator
    
    a = FoscNote(60, 1/4);
    a.attach(FoscDynamic('p'));
    a.prHasIndicator;
    a.prHasIndicator(FoscDynamic);
    a.prHasIndicator(FoscMetronomeMark);
    -------------------------------------------------------------------------------------------------------- */
    prHasIndicator { |type, attributes|
        var indicators;
        indicators = this.prGetIndicators(type, attributes);
        ^indicators.notEmpty;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prImmediatelyPrecedes
    
`   a = FoscNote(60, 1/4);
    b = FoscNote(60, 1/4);
    c = FoscNote(60, 1/4);
    FoscVoice([a, b, c]);

    a.prImmediatelyPrecedes(b);      // true
    b.prImmediatelyPrecedes(c);      // true
    c.prImmediatelyPrecedes(a);      // false
    -------------------------------------------------------------------------------------------------------- */
    prImmediatelyPrecedes { |component|
        var successors, current, sibling;
        
        successors = [];
        current = this;
        
        block { |break|
            while { current.notNil } {
                sibling = current.prSibling(1);
                //!!!TODO: sibling = current.prSiblingWithGraces(1);
                if (sibling.isNil) {
                    current = current.parent;
                } {
                    successors = sibling.prGetDescendantsStartingWith;
                    break.value;
                };
            };
        };
        
        ^successors.includes(component);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prMoveIndicators

    a = FoscNote(60, 1/4);
    a.attach(FoscDynamic('p'));
    b = FoscNote(60, 1/4);

    a.prHasIndicator(FoscDynamic);
    b.prHasIndicator(FoscDynamic);

    a.prMoveIndicators(b);

    a.prHasIndicator(FoscDynamic);
    b.prHasIndicator(FoscDynamic);
    -------------------------------------------------------------------------------------------------------- */
    prMoveIndicators { |recipientComponent|
        wrappers.do { |wrapper|
            this.detach(wrapper);
            recipientComponent.attach(wrapper);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prRemoveFromParent

    a = FoscNote(60, 1/4);
    b = FoscVoice([a]);
    a.parent;
    b.components;

    a.prRemoveFromParent;
    a.parent;
    b.components;
    -------------------------------------------------------------------------------------------------------- */
    prRemoveFromParent {
        this.prUpdateLater(offsets: true);
        
        this.prGetParentage[1..].do { |component|
            if (component.isKindOf(FoscContext)) {
                component.dependentWrappers.do { |wrapper|
                    if (wrapper.component == this) { component.dependentWrappers.remove(wrapper) };
                };
            };
        };
        
        if (parent.notNil) { parent.components.remove(this) };
        parent = nil;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prRemoveNamedChildrenFromParentage
    -------------------------------------------------------------------------------------------------------- */
    prRemoveNamedChildrenFromParentage { |nameDictionary|
        var namedChildren;
        
        if (parent.notNil && nameDictionary.notNil) {
            this.prGetParentage[1..].do { |parent|
                namedChildren = parent.namedChildren;
                
                nameDictionary.keys.do { |name|
                    nameDictionary[name].do { |component| namedChildren[name].remove(component) };
                    if (namedChildren[name].isEmpty) { namedChildren.removeAt(name) };
                };
            };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prRestoreNamedChildrenToParentage
    -------------------------------------------------------------------------------------------------------- */
    prRestoreNamedChildrenToParentage { |nameDictionary|
        var namedChildren;
        
        if (nameDictionary.notEmpty) {
            this.prGetParentage[1..].do { |parent|
                namedChildren = parent.namedChildren;
                
                nameDictionary.keys.do { |name|
                    if (namedChildren[name].notNil) {
                        namedChildren[name] = namedChildren[name].union(nameDictionary[name]);
                    } {
                        namedChildren[name] = nameDictionary[name].copy;
                    };
                };
            };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prSetParent
    -------------------------------------------------------------------------------------------------------- */
    prSetParent { |newParent|
        var namedChildren;
        
        namedChildren = this.prCacheNamedChildren;
        this.prRemoveNamedChildrenFromParentage(namedChildren);
        this.prRemoveFromParent;
        parent = newParent;
        this.prRestoreNamedChildrenToParentage(namedChildren);
        this.prUpdateLater(offsets: true);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prSibling
    
    a = FoscNote(60, 1/4);
    b = FoscNote(60, 1/4);
    c = FoscNote(60, 1/4);
    FoscVoice([a, b, c]);
    
    a.prSibling(1) === b;       // true
    b.prSibling(-1) === a;      // true
    a.prSibling(-1) === b;      // false
    -------------------------------------------------------------------------------------------------------- */
    prSibling { |n|
        var getSibling, sibling, componentParent, index;
        
        assert(
            #[-1, 1].includes(n),
            "%:%: n must be -1 or 1: %.".format(this.species, thisMethod.name, n);
        );

        getSibling = { |component, n|
            componentParent = component.parent;
            case
            { componentParent.isNil } { nil }
            { componentParent.isSimultaneous } { nil }
            {
                index = componentParent.indexOf(component) + n;
                if ((0 <= index) && { index < componentParent.size }) { componentParent[index] } { nil };
            };
        };

        this.prGetParentage.do { |parent|
            //!!! OLD: sibling = parent.prSibling2(n.sign);
            sibling = getSibling.(parent, n.sign);
            if (sibling.notNil) { ^sibling };
        };
        
        ^nil;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prUpdateLater
    -------------------------------------------------------------------------------------------------------- */
    prUpdateLater { |offsets=false, offsetsInSeconds=false|
        if (offsets.not && offsetsInSeconds.not) {
            ^throw("%:%: either offsets or offsetsInSeconds must be true."
                .format(this.species, thisMethod.name))
        };
        
        this.prGetParentage.do { |component|
            case
            { offsets } {
                component.instVarPut('offsetsAreCurrent', false);
            } {
                component.instVarPut('offsetsInSecondsAreCurrent', false);
            };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • prUpdateMeasureNumbers
    -------------------------------------------------------------------------------------------------------- */
    prUpdateMeasureNumbers {
        FoscUpdateManager().prUpdateMeasureNumbers(this);
    }
    /* --------------------------------------------------------------------------------------------------------
    • prUpdateNow
    -------------------------------------------------------------------------------------------------------- */
    prUpdateNow { |offsets=false, offsetsInSeconds=false, indicators=false|
        ^FoscUpdateManager().prUpdateNow(this, offsets, offsetsInSeconds, indicators);
    }
}
