/* ------------------------------------------------------------------------------------------------------------
• FoscObject
------------------------------------------------------------------------------------------------------------ */
FoscObject {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC INSTANCE METHODS: SPECIAL METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • ==

    Is true when ID of 'object' equals ID of Abjad object. Otherwise false.

    Returns true or false.

    def __eq__(self, object):
        return id(self) == id(object)
	-------------------------------------------------------------------------------------------------------- */
	// == { |object|
	// 	^(this != object).not;
	// }
	/* --------------------------------------------------------------------------------------------------------
    • !-
	-------------------------------------------------------------------------------------------------------- */
	!= { |object|
		^(this == object).not;
	}
   	/* --------------------------------------------------------------------------------------------------------
    • >
	-------------------------------------------------------------------------------------------------------- */
	// > { |object|
	// 	^(this <= object).not;
	// }
	/* --------------------------------------------------------------------------------------------------------
    • >=
	-------------------------------------------------------------------------------------------------------- */
	// >= { |object|
	// 	^(this < object).not;
	// }
	/* --------------------------------------------------------------------------------------------------------
    • >
	-------------------------------------------------------------------------------------------------------- */
	< { |object|
		^(this >= object).not;
	}
	/* --------------------------------------------------------------------------------------------------------
    • >=
	-------------------------------------------------------------------------------------------------------- */
	<= { |object|
		^(this > object).not;
	}
	/* --------------------------------------------------------------------------------------------------------
    • add (python: __add__)
	-------------------------------------------------------------------------------------------------------- */
	+ { |object|
		^this.add(object);
	}
	/* --------------------------------------------------------------------------------------------------------
    • asInteger
	-------------------------------------------------------------------------------------------------------- */
	asInt {
    	^this.asInteger;
    }
	/* --------------------------------------------------------------------------------------------------------
    • concat (python: __add__)
	-------------------------------------------------------------------------------------------------------- */
	++ { |object|
		^this.concat(object);
	}
	/* --------------------------------------------------------------------------------------------------------
    • div (python: __div_)
	-------------------------------------------------------------------------------------------------------- */
	/ { |object|
		^this.div(object);
	}
	/* --------------------------------------------------------------------------------------------------------
    • dup (python: __mult__)
	-------------------------------------------------------------------------------------------------------- */
	! { |object|
		^this.dup(object);
	}
	/* --------------------------------------------------------------------------------------------------------
    • mul (python: __mul__)
	-------------------------------------------------------------------------------------------------------- */
	* { |object|
		^this.mul(object);
	}
	/* --------------------------------------------------------------------------------------------------------
    • sub (python: __sub__)
	-------------------------------------------------------------------------------------------------------- */
	- { |object|
		^this.sub(object);
	}
	// SET OPERATIONS
	/* --------------------------------------------------------------------------------------------------------
    • difference
    //!!! - must be an override at a lower point in the hierarchy
	-------------------------------------------------------------------------------------------------------- */
	// - { |object|
 	//        ^this.difference(object)
 	//    }
    /* --------------------------------------------------------------------------------------------------------
    • difference
	-------------------------------------------------------------------------------------------------------- */
    & { |object|
        ^this.intersection(object);
    }
     /* --------------------------------------------------------------------------------------------------------
    • symmetricDifference
	-------------------------------------------------------------------------------------------------------- */
    -- { |object|
        ^this.symmetricDifference(object);
    }
     /* --------------------------------------------------------------------------------------------------------
    • union
	-------------------------------------------------------------------------------------------------------- */
    | { |object|
        ^this.union(object);
    }
    /* --------------------------------------------------------------------------------------------------------
    • format

    Formats Abjad object.

	Returns string.
    -------------------------------------------------------------------------------------------------------- */
    format {
    	^this.str;
    }
    /* --------------------------------------------------------------------------------------------------------
    • ps
    -------------------------------------------------------------------------------------------------------- */
    ps {
        ^this.pitchString;
    }
    /* --------------------------------------------------------------------------------------------------------
    • str
    -------------------------------------------------------------------------------------------------------- */
    str {
        ^this.prGetLilypondFormat;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prCopyKeywords

    !!!TODO: rename: method to prCopyInstVars, keywords arg to instVarNames
    !!!TODO: should 'val' placed in new object be copied or deepCopied ??
    -------------------------------------------------------------------------------------------------------- */
    prCopyKeywords { |new, keywords|
        assert(keywords.isSequenceableCollection);
        this.instVarDict.keysValuesDo { |key, val|
            if (keywords.includes(key)) { new.instVarPut(key, val) };
        };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC METHODS: TOP LEVEL
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • annotate

    a = FoscNote(60, 1/4);
    a.annotate('foo', FoscClef('bass'));
    FoscInspection(a).annotation('foo');
    -------------------------------------------------------------------------------------------------------- */
    annotate { |annotation, indicator|
        var wrapper;
        //!!!TODO: asser(annotation.isKindOf(Symbol)); // String as well ??
        // receiver must be a subclass of FoscComponent !!! -- move this method to FoscComponent ?
        wrapper = FoscWrapper(
            component: this,
            indicator: indicator,
            annotation: annotation
        );
    }
    /* --------------------------------------------------------------------------------------------------------
    • attach

    !!!TODO: attaches to FoscComponents and FoscSelections? can this method be moved lower in the tree?
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
    attach { |attachment, context, deactivate, syntheticOffset, tag, wrapper=false|
        var target, nonIndicatorPrototype, result, message, graceContainer, isAcceptable, component;
        var annotation, indicator, localWrapper;

        target = this;
        assert(attachment.notNil, "%:attach: attachment cannot be nil.".format(target));
        nonIndicatorPrototype = [FoscAfterGraceContainer, FoscGraceContainer];

        if (context.notNil && nonIndicatorPrototype.any { |type| attachment.isKindOf(type) }) {
            throw("%:attach: set context for indicators, not %.".format(target, attachment));
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
                throw(message);
            };
        };

        graceContainer = [FoscAfterGraceContainer, FoscGraceContainer];

        case
        { graceContainer.any { |type| attachment.isKindOf(type) } } {
            if (target.isKindOf(FoscLeaf).not) {
                throw("%:attach: grace containers attach to a single leaf.".format(target));
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
                throw("Can't attach % to a container: %.".format(attachment, target));
            };
        }
        { target.isKindOf(FoscLeaf).not } {
            throw("%:attach: indicator % must attach to leaf, not: %.".format(target, attachment, target));
        };

        component = target;
        assert(component.isKindOf(FoscComponent));

        annotation = nil;

        //!!! FoscPlaybackCommand -- TESTING
        if (attachment.isKindOf(FoscPlaybackCommand)) {
            indicator = attachment.prGetIndicator;
            if (indicator.notNil) { this.attach(indicator.copy) };
        };
        //!!!

        if (attachment.isKindOf(FoscWrapper)) {
            annotation = attachment.annotation;
            context = context ?? { attachment.context };
            deactivate = deactivate ?? { attachment.deactivate };
            syntheticOffset = syntheticOffset ?? { attachment.syntheticOffset };
            tag = tag ?? { attachment.tag };
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
            syntheticOffset: syntheticOffset,
            tag: tag
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
    • iterate
    -------------------------------------------------------------------------------------------------------- */
    iterate {
        ^FoscIteration(this);
    }
    /* --------------------------------------------------------------------------------------------------------
    • mutate
    -------------------------------------------------------------------------------------------------------- */
    mutate {
		^FoscMutation(this);
	}
    /* --------------------------------------------------------------------------------------------------------
    • override


    • Example 1

    a = FoscNote(60, 1/4);
    override(a).noteHead.color = 'red';
    override(a).noteHead.size = 12;
    a.format;
    -------------------------------------------------------------------------------------------------------- */
    override {
        if (this.respondsTo('overrides').not) {
            throw("%: does not respond to override.".format(this.species));
        };
        if (this.overrides.isNil) {
            this.instVarPut('overrides', FoscLilypondGrobNameManager());
        };
        ^this.overrides;
    }
    /* --------------------------------------------------------------------------------------------------------
    • setting
    -------------------------------------------------------------------------------------------------------- */
    //!!!TODO: deprecate in favour of 'set'
    setting {
        ^this.set;
    }
    /* --------------------------------------------------------------------------------------------------------
    • set

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], 1/8));
    set(a).instrumentName = FoscMarkup("Violin");
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    set {
        if (this.respondsTo('lilypondSettingNameManager').not) {
            throw("%: does not respond to set.".format(this.species));
        };
        if (this.lilypondSettingNameManager.isNil) {
            this.instVarPut('lilypondSettingNameManager', FoscLilypondSettingNameManager());
        };
        ^this.lilypondSettingNameManager;
    }
    /* --------------------------------------------------------------------------------------------------------
    • select
    -------------------------------------------------------------------------------------------------------- */
    select {
        ^FoscSelection(this);
    }
    /* --------------------------------------------------------------------------------------------------------
    • show

    Shows 'object'.

	Makes LilyPond input files and output PDF.

    Writes LilyPond input file and output PDF to Abjad output directory.

    Opens output PDF.


    • Example 1

    a = FoscNote(60, 1/4);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
	show { |path|
		var result, pdfPath, success;
        if (this.respondsTo('illustrate').not) {
            throw("% does not respond to 'illustrate' and can't be shown.".format(this));
        };
        //if (args.notEmpty) { path = args[0] };
        result = FoscPersistenceManager(this).asPDF(path);
        # pdfPath, success = result;
        //"pdfPath: ".post; pdfPath.postln;
        if (success) { FoscIOManager.openFile(pdfPath) };
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


    ### abjad:
    \new Staff
    {
        c'4
        - \tweak color #red
        ^ \markup { "Allegro assai" }
        d'4
        e'4
        f'4
    }
    ###

    Survives copy:

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65], [1/4]));
    m = FoscMarkup('Allegro assai', direction: 'above');
    tweak(m).color = 'red';
    n = m.copy;
    a.leafAt(0).attach(n);
    //a.show;
    a.format;

    ### abjad:
    \new Staff
    {
        c'4
        - \tweak color #red
        ^ \markup { "Allegro assai" }
        d'4
        e'4
        f'4
    }
    ###

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

    !!!TODO: NOT YET IMPLEMENTED FOR FoscComponents

    Tweaks note-head:

    a = FoscStaff(FoscLeafMaker().(#[60,61,62,63], [1/4]));
    tweak(a[1].noteHead).color = 'red';
    a.show;
    a.format;

    ### abjad:
    \new Staff
    {
        c'4
        \tweak color #red
        cs'4
        d'4
        ds'4
    }
    ###


    !!!TODO: NOT YET IMPLEMENTED FOR FoscComponents

    Tweaks grob aggregated to note-head:

    a = FoscStaff(FoscLeafMaker().(#[60,61,62,63], [1/4]));
    tweak(a[1].noteHead).accidental.color = 'red';
    a.show;
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
    b = FoscLilypondTweakManager();
    b.setTweaks(a, #[['dynamicLineSpanner', 5]]);
    a.tweaks;
    a.tweaks.prGetAttributePairs;


    • Example Y

    a = FoscHorizontalBracket();
    tweak(a).color = 'red';
    tweak(a).staffPadding = 5;
    a.tweaks.prGetAttributePairs;
    -------------------------------------------------------------------------------------------------------- */
    // abjad 3.0
    tweak {
        var constants, prototype, manager;
        if (this.respondsTo('tweaks').not) {
            throw("% does not respond to tweak.".format(this.species));
        };
        constants = #['above', 'below', 'down', 'left', 'right', 'up'];
        prototype = [Boolean, SimpleNumber, String, SequenceableCollection, FoscScheme];
        if (constants.includes(this) || { prototype.any { |type| this.isKindOf(type) } }) {
            manager = FoscLilypondTweakManager();
            manager.pendingValue_(this);
            ^manager;
        };
        if (this.tweaks.isNil) {
            //this.prTweaks_(FoscLilypondTweakManager());
            this.instVarPut('tweaks', FoscLilypondTweakManager());
        };
        ^this.tweaks;
    }
    /* --------------------------------------------------------------------------------------------------------
    • write
	-------------------------------------------------------------------------------------------------------- */
	write {
		^FoscPersistenceManager(this);
	}
    /* --------------------------------------------------------------------------------------------------------
    • writeLY
    -------------------------------------------------------------------------------------------------------- */
    writeLY { |path|
        ^FoscPersistenceManager(this).asLY(path);
    }
    /* --------------------------------------------------------------------------------------------------------
    • writeMIDI
    -------------------------------------------------------------------------------------------------------- */
    writeMIDI { |path|
        ^FoscPersistenceManager(this).asMIDI(path);
    }
    /* --------------------------------------------------------------------------------------------------------
    • writePDF
    -------------------------------------------------------------------------------------------------------- */
    writePDF { |path|
        ^FoscPersistenceManager(this).asPDF(path);
    }
    /* --------------------------------------------------------------------------------------------------------
    • writePNG
    -------------------------------------------------------------------------------------------------------- */
    writePNG { |path, resolution=100|
        ^FoscPersistenceManager(this).asPNG(path, resolution: resolution);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: FoscSelection / FoscCopmonent shared interface
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • doComponents

    • iterate notes in a selection

    a = FoscLeafMaker().(#[60,62,64,65,67,69], [1/8]);
    a.doComponents({ |each, i| each.cs.postln }, FoscNote);

    • reverse iterate notes in a selection

    a = FoscLeafMaker().(#[60,62,64,65,67,69], [1/8]);
    a.doComponents({ |each, i| each.cs.postln }, FoscNote, reverse: true);

    • iterate notes in a staff

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65,67,69], [1/8]));
    a.doComponents({ |each, i| each.cs.postln }, FoscNote);

    • iterate all components in a staff

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65,67,69], [1/8]));
    a.doComponents({ |each, i| each.cs.postln });

    • iterate leaf

    a = FoscNote(60, 1/4);
    a.doComponents({ |each, i| each.cs.postln }, FoscNote);

    • throw error

    a = FoscDynamic('p');
    a.doComponents({ |each, i| each.cs.postln });
    -------------------------------------------------------------------------------------------------------- */
    doComponents { |function, prototype, exclude, doNotIterateGraceContainers=false, graceNotes=false,
        reverse=false|
        var iterator;
        FoscObject.prCheckIsIterable(this, thisMethod);
        iterator = FoscIteration(this).components(
            prototype: prototype,
            exclude: exclude,
            doNotIterateGraceContainers: doNotIterateGraceContainers,
            graceNotes: graceNotes,
            reverse: reverse
        );
        iterator.do(function);
    }
    /* --------------------------------------------------------------------------------------------------------
    • doLeaves

    • iterate all leaves

    a = FoscLeafMaker().(#[60,62,nil,65,67,nil], [1/8]);
    a.doLeaves { |each| each.cs.postln };

    • iterate pitched leaves

    a = FoscLeafMaker().(#[60,62,nil,65,67,nil], [1/8]);
    a.doLeaves({ |each| each.cs.postln }, pitched: true);

    • iterate non-pitched leaves

    a = FoscLeafMaker().(#[60,62,nil,65,67,nil], [1/8]);
    a.doLeaves({ |each| each.cs.postln }, pitched: false);
    -------------------------------------------------------------------------------------------------------- */
    doLeaves { |function, pitched, prototype, exclude, doNotIterateGraceContainers=false,
        graceNotes=false, reverse=false|
        var iterator;
        FoscObject.prCheckIsIterable(this, thisMethod);
        iterator = FoscIteration(this).leaves(
            prototype: prototype,
            exclude: exclude,
            doNotIterateGraceContainers: doNotIterateGraceContainers,
            graceNotes: graceNotes,
            pitched: pitched,
            reverse: reverse
        );
        iterator.do(function);
    }
    /* --------------------------------------------------------------------------------------------------------
    • doLogicalTies

    a = FoscStaff(FoscLeafMaker().(#[60,60,62,nil,64,64], [1/4,1/24,1/12,1/8,1/4,1/4]));
    m = a.selectLeaves;
    tie(m[0..1]);
    tie(m[4..]);
    a.show;

    a.selectLogicalTies.items.do { |each| each.items.collect { |each| each.cs }.postln };

    • iterate all logicalTies

    a.doLogicalTies { |each| each.items.collect { |each| each.cs }.postln };

    • iterate pitched logicalTies

    a.doLogicalTies({ |each| each.items.collect { |each| each.cs }.postln }, pitched: true);

    • iterate non-pitched logicalTies

    a.doLogicalTies({ |each| each.items.collect { |each| each.cs }.postln }, pitched: false);

    • iterate nontrivial logicalTies

    a.doLogicalTies({ |each| each.items.collect { |each| each.cs }.postln }, nontrivial: true);

    • iterate trivial logicalTies

    a.doLogicalTies({ |each| each.items.collect { |each| each.cs }.postln }, nontrivial: false);

    • iterate logicalTies in reverse order

    a.doLogicalTies({ |each| each.items.collect { |each| each.cs }.postln }, reverse: true);
    -------------------------------------------------------------------------------------------------------- */
    doLogicalTies { |function, exclude, graceNotes=false, nontrivial, pitched, reverse=false|
        var iterator;
        FoscObject.prCheckIsIterable(this, thisMethod);
        iterator = FoscIteration(this).logicalTies(
            exclude: exclude,
            graceNotes: graceNotes,
            nontrivial: nontrivial,
            pitched: pitched,
            reverse: reverse
        );
        iterator = all(iterator); //!!! TEMPORARY - REMOVE
        iterator.do(function);
    }
    /* --------------------------------------------------------------------------------------------------------
    • doRuns

    • Attach horizontal bracket to each run.

    a = FoscStaff(FoscLeafMaker().(#[60,60,62,nil,64,64], [1/4,1/24,1/12,1/8,1/4,1/4]));
    a.consistsCommands.add('Horizontal_bracket_engraver');
    m = a.selectLeaves;
    tie(m[0..1]);
    tie(m[4..]);
    t = #['bracket-flare', [0,0], 'color', 'red', 'direction', 'up', 'staff-padding', 5];
    a.doRuns { |each| each.horizontalBracket(tweaks: t) };
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    doRuns { |function, exclude|
        FoscObject.prCheckIsIterable(this, thisMethod);
        ^this.selectRuns(exclude: exclude).do(function);
    }
    /* --------------------------------------------------------------------------------------------------------
    • doTimeline

    • iterate all leaves

    a = FoscStaff(FoscLeafMaker().((60..67), [1/8]));
    b = FoscStaff(FoscLeafMaker().((60..63), [1/4]));
    c = FoscScore([a, b]);
    c.doTimeline { |each, i| each.attach(FoscMarkup(i.asString, 'above')) };
    c.show;

    • iterate all leaves in reverse

    a = FoscStaff(FoscLeafMaker().((60..67), [1/8]));
    b = FoscStaff(FoscLeafMaker().((60..63), [1/4]));
    c = FoscScore([a, b]);
    c.doTimeline({ |each, i| each.attach(FoscMarkup(i.asString, 'above')) }, reverse: true);
    c.show;

    • iterate pitched leaves

    a = FoscStaff(FoscLeafMaker().([60,61,nil,63,nil,nil,65], [1/8]));
    b = FoscStaff(FoscLeafMaker().((60..63), [1/4]));
    c = FoscScore([a, b]);
    c.doTimeline({ |each, i| each.attach(FoscMarkup(i, 'above')) }, pitched: true);
    c.show;

    • iterate non-pitched leaves

    a = FoscStaff(FoscLeafMaker().([60,61,nil,63,nil,nil,65], [1/8]));
    b = FoscStaff(FoscLeafMaker().((60..63), [1/4]));
    c = FoscScore([a, b]);
    c.doTimeline({ |each, i| each.attach(FoscMarkup(i, 'above')) }, pitched: false);
    c.show;
    -------------------------------------------------------------------------------------------------------- */
    doTimeline { |function, prototype, exclude, pitched, reverse=false|
        var iterator;
        FoscObject.prCheckIsIterable(this, thisMethod);
        iterator = FoscIteration(this).timeline(
            prototype: prototype,
            exclude: exclude,
            pitched: pitched,
            reverse: reverse
        );
        iterator.do(function);
    }
    /* --------------------------------------------------------------------------------------------------------
    • doTimelineByLogicalTies

    • iterate logical ties

    a = FoscStaff(FoscLeafMaker().((60..67), [5/32]));
    b = FoscStaff(FoscLeafMaker().((60..63), [5/16]));
    c = FoscScore([a, b]);
    c.doTimelineByLogicalTies({ |each, i| each.head.attach(FoscMarkup(i, 'above')) });
    c.show;

    • iterate pitched logical ties

    a = FoscStaff(FoscLeafMaker().(#[60,61,nil,63,nil,nil,65], [5/32]));
    b = FoscStaff(FoscLeafMaker().((60..63), [5/16]));
    c = FoscScore([a, b]);
    c.doTimelineByLogicalTies({ |each, i| each.head.attach(FoscMarkup(i, 'above')) }, pitched: true);
    c.show;
    -------------------------------------------------------------------------------------------------------- */
    doTimelineByLogicalTies { |function, exclude, pitched, reverse=false|
        var iterator;
        FoscObject.prCheckIsIterable(this, thisMethod);
        iterator = FoscIteration(this).timelineByLogicalTies(
            exclude: exclude,
            pitched: pitched,
            reverse: reverse
        );
        iterator.do(function);
    }
    /* --------------------------------------------------------------------------------------------------------
    • leafAt

    • selection

    a = FoscSelection([FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    a.leafAt(1).str;

    • selection: pitched

    a = FoscSelection([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    a.leafAt(0, pitched: true).str;

    • container

    a = FoscStaff([FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    a.leafAt(1).str;

    • container: pitched

    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    a.leafAt(0, pitched: true).str;
    -------------------------------------------------------------------------------------------------------- */
    leafAt { |index, pitched|
        FoscIteration(this).leaves(pitched: pitched).do { |each, i| if (i == index) { ^each } };
        ^nil;
    }
    /* --------------------------------------------------------------------------------------------------------
    • selectComponents

    • select all components

    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = a.selectComponents;
    b.do { |each| each.str.postln };

    • select notes

    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = a.selectComponents(prototype: FoscNote);
    b.do { |each| each.str.postln };

    • select notes and rests

    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = a.selectComponents(prototype: [FoscNote, FoscRest]);
    b.do { |each| each.str.postln };

    • select notes and rests in reverse order

    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = a.selectComponents(prototype: [FoscNote, FoscRest], reverse: true);
    b.do { |each| each.str.postln };
    -------------------------------------------------------------------------------------------------------- */
    selectComponents { |prototype, exclude, graceNotes=false, reverse=false|
        FoscObject.prCheckIsIterable(this, thisMethod);

        ^FoscSelection(this).components(
            prototype: prototype,
            exclude: exclude,
            graceNotes: graceNotes,
            reverse: reverse
        );
    }
    /* --------------------------------------------------------------------------------------------------------
    • selectLeaves

    • select all leaves

    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = a.selectLeaves;
    b.do { |each| each.str.postln };

    • select pitched leaves

    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = a.selectLeaves(pitched: true);
    b.do { |each| each.str.postln };

    • select non-pitched leaves

    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = a.selectLeaves(pitched: false);
    b.do { |each| each.str.postln };

    • select leaves in reverse order

    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = a.selectLeaves(reverse: true);
    b.do { |each| each.str.postln };
    -------------------------------------------------------------------------------------------------------- */
    selectLeaves { |prototype, exclude, graceNotes=false, pitched, reverse=false|
        FoscObject.prCheckIsIterable(this, thisMethod);

        ^FoscSelection(this).leaves(
            prototype: prototype,
            exclude: exclude,
            graceNotes: graceNotes,
            pitched: pitched,
            reverse: reverse
        );
    }
    /* --------------------------------------------------------------------------------------------------------
    • selectLogicalTies

    Selects logical ties.

    Returns new selection.


    a = FoscStaff(FoscLeafMaker().(#[60,60,62,nil,64,64], [1/4,1/24,1/12,1/8,1/4,1/4]));
    m = a.selectLeaves;
    tie(m[0..1]);
    tie(m[4..]);
    a.show;

    • select all logicalTies

    b = a.selectLogicalTies;
    b.do { |each| each.items.collect { |each| each.cs }.postln };

    • select pitched logicalTies

    b = a.selectLogicalTies(pitched: true);
    b.do { |each| each.items.collect { |each| each.cs }.postln };

    • select non-pitched logicalTies

    b = a.selectLogicalTies(pitched: false);
    b.do { |each| each.items.collect { |each| each.cs }.postln };

    • select nontrivial logicalTies

    b = a.selectLogicalTies(nontrivial: true);
    b.do { |each| each.items.collect { |each| each.cs }.postln };

    • select trivial logicalTies

    b = a.selectLogicalTies(nontrivial: false);
    b.do { |each| each.items.collect { |each| each.cs }.postln };

    • select logicalTies in reverse order

    b = a.selectLogicalTies(reverse: true);
    b.do { |each| each.items.collect { |each| each.cs }.postln };
    -------------------------------------------------------------------------------------------------------- */
    selectLogicalTies { |exclude, graceNotes=false, nontrivial, pitched, reverse=false|
        FoscObject.prCheckIsIterable(this, thisMethod);

        ^FoscSelection(this).logicalTies(
            exclude: exclude,
            graceNotes: graceNotes,
            nontrivial: nontrivial,
            pitched: pitched,
            reverse: reverse
        );
    }
    /* --------------------------------------------------------------------------------------------------------
    • selectRuns

    Select runs.

    • Attach horizontal bracket to each run.

    a = FoscStaff(FoscLeafMaker().(#[60,60,62,nil,64,64], [1/4,1/24,1/12,1/8,1/4,1/4]));
    a.consistsCommands.add('Horizontal_bracket_engraver');
    m = a.selectLeaves;
    tie(m[0..1]);
    tie(m[4..]);
    a.selectRuns.do { |each| each.horizontalBracket(tweaks: #[['direction', 'up']]) };
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    selectRuns { |exclude|
        FoscObject.prCheckIsIterable(this, thisMethod);
        ^FoscSelection(this).leaves(exclude: exclude).runs;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PLAYBACK
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • isPlaying
    -------------------------------------------------------------------------------------------------------- */
    isPlaying {
        if (this.respondsTo('player')) { ^this.player.isPlaying };
    }
    /* --------------------------------------------------------------------------------------------------------
    • pause
    -------------------------------------------------------------------------------------------------------- */
    pause {
        if (this.respondsTo('player')) { this.player.pause };
    }
    /* --------------------------------------------------------------------------------------------------------
    • play
    -------------------------------------------------------------------------------------------------------- */
    play {
        var player;
        
        if (this.respondsTo('player')) {
            player = this.player;
            
            if (player.isNil) {
                this.instVarPut('player', FoscPlayer(this).play);
            } {
                player.play;
            };
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • resume
    -------------------------------------------------------------------------------------------------------- */
    resume {
        if (this.respondsTo('player')) { this.player.resume };
    }
    /* --------------------------------------------------------------------------------------------------------
    • *stop
    -------------------------------------------------------------------------------------------------------- */
    stop {
        if (this.respondsTo('player')) { this.player.stop };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *prCheckIsIterable
    -------------------------------------------------------------------------------------------------------- */
    *prCheckIsIterable { |object, method|
        var prototype, isIterable;
        prototype = [FoscComponent, FoscSelection, SequenceableCollection];
        isIterable = prototype.any { |type| object.isKindOf(type) };
        if (isIterable.not) {
            throw("%: receiver is not iterable: %.".format(method.name, object));
        };
    }
}
