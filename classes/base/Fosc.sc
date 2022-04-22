/* ------------------------------------------------------------------------------------------------------------
• Fosc
------------------------------------------------------------------------------------------------------------ */
Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS METHODS: CONFIGURATION
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    classvar lilypondExecutablePath="lilypond";
    /* --------------------------------------------------------------------------------------------------------
    • *initClass
    -------------------------------------------------------------------------------------------------------- */
    *initClass {
        var stylesheetsDir, returnCode;

        if (File.exists(this.outputDirectory).not) {
            File.mkdir(this.outputDirectory);
        };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *lilypondVersionString
    
    Fosc.lilypondVersionString;
    -------------------------------------------------------------------------------------------------------- */
    *lilypondVersionString {
        var executablePath, str, versionString;
        executablePath = this.lilypondExecutablePath;
        ^versionString ?? {
            str = (executablePath + "--version").unixCmdGetStdOut;
            str.copyRange(*[str.findRegexp("\\s[0-9]")[0][0]+1, str.find("\n")-1]);
        };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE CLASS PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *outputDirectory

    Fosc.outputDirectory;
    -------------------------------------------------------------------------------------------------------- */
    *outputDirectory {
        ^"%/fosc-output".format(Platform.userConfigDir);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *rootDirectory

    Fosc.rootDirectory;
    -------------------------------------------------------------------------------------------------------- */
    *rootDirectory {
        ^"%/fosc".format(Platform.userExtensionDir);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *stylesheetDirectory

    Fosc.stylesheetDirectory;
    -------------------------------------------------------------------------------------------------------- */
    *stylesheetDirectory {
        ^"%/stylesheets".format(this.rootDirectory);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *lilypondExecutablePath

    Fosc.lilypondExecutablePath;
    -------------------------------------------------------------------------------------------------------- */ 
    *lilypondExecutablePath {
        if (lilypondExecutablePath.isNil || { File.exists(lilypondExecutablePath).not }) {
            error("Lilypond executable not found at: %.".format(lilypondExecutablePath));
            ^nil;
        } {
            ^lilypondExecutablePath;  
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • *lilypondExecutablePath_

    The executable path should be set in the user startup file, e.g.:

    Fosc.lilypondExecutablePath = "/Applications/LilyPond.app/Contents/Resources/bin/lilypond";

    or:

    Fosc.lilypondExecutablePath = "/usr/local/bin/lilypond";
    -------------------------------------------------------------------------------------------------------- */ 
    *lilypondExecutablePath_ { |path|
        lilypondExecutablePath = path;
    }
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC INSTANCE METHODS: SPECIAL METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • ==
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
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: TOP LEVEL
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • format
    
    Formats component.

    Returns string.
    -------------------------------------------------------------------------------------------------------- */
    format {
        if (this.respondsTo('prGetLilypondFormat').not) {
            throw("% does not respond to 'prGetLilypondFormat'.".format(this));
        };

        ^this.prGetLilypondFormat;
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
    • select
    -------------------------------------------------------------------------------------------------------- */
    select {
        ^FoscSelection(this);
    }
    /* --------------------------------------------------------------------------------------------------------
    • show

    !!!TODO: move into FoscComponent?

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
        
        if (success) { FoscIOManager.openFile(pdfPath) };
	}
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // !!! TODO: move write methods out of Fosc and into FoscComponent
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
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
    // PUBLIC INSTANCE METHODS: FoscSelection / FoscComponent shared interface
    // !!! TODO: move these out of Fosc and into FoscSelection / FoscComponent
    // !!! NB: most of these methods are also implemented by SequenceableCollection
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • doComponents


    • Example 1

    Iterate notes in a selection

    a = FoscLeafMaker().(#[60,62,64,65,67,69], [1/8]);
    a.doComponents({ |each, i| each.cs.postln }, FoscNote);


    • Example 2

    Reverse iterate notes in a selection

    a = FoscLeafMaker().(#[60,62,64,65,67,69], [1/8]);
    a.doComponents({ |each, i| each.cs.postln }, FoscNote, reverse: true);


    • Example 3

    Iterate notes in a staff

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65,67,69], [1/8]));
    a.doComponents({ |each, i| each.cs.postln }, FoscNote);


    • Example 4

    Iterate all components in a staff

    a = FoscStaff(FoscLeafMaker().(#[60,62,64,65,67,69], [1/8]));
    a.doComponents({ |each, i| each.cs.postln });


    • Example 5

    Iterate leaf

    a = FoscNote(60, 1/4);
    a.doComponents({ |each, i| each.cs.postln }, FoscNote);
    -------------------------------------------------------------------------------------------------------- */
    doComponents { |function, prototype, exclude, doNotIterateGraceContainers=false, graceNotes=false,
        reverse=false|
        Fosc.prCheckIsIterable(this, thisMethod);        
        FoscIteration(this).components(
            prototype, exclude, doNotIterateGraceContainers, graceNotes, reverse
        ).do(function);
    }
    /* --------------------------------------------------------------------------------------------------------
    • doLeaves


    • Example 1

    Iterate all leaves

    a = FoscLeafMaker().(#[60,62,nil,65,67,nil], [1/8]);
    a.doLeaves { |each| each.cs.postln };


    • Example 2

    Iterate pitched leaves

    a = FoscLeafMaker().(#[60,62,nil,65,67,nil], [1/8]);
    a.doLeaves({ |each| each.cs.postln }, pitched: true);


    • Example 3

    Iterate non-pitched leaves

    a = FoscLeafMaker().(#[60,62,nil,65,67,nil], [1/8]);
    a.doLeaves({ |each| each.cs.postln }, pitched: false);
    -------------------------------------------------------------------------------------------------------- */
    doLeaves { |function, prototype, pitched, graceNotes=false|
        Fosc.prCheckIsIterable(this, thisMethod);
        FoscIteration(this).leaves(prototype, pitched, graceNotes).do(function);
    }
    /* --------------------------------------------------------------------------------------------------------
    • doLogicalTies


    • Example 1

    Iterate all logicalTies

    a = FoscMusicMaker();
    b = a.(durations: 1/4 ! 4, divisions: #[[1,1,1,1,1]], mask: #[2,4,-1], pitches: "c' d' ef' f'");
    b.doLogicalTies { |each| each.cs.postln };


    • Example 2

    Iterate pitched logicalTies

    a = FoscMusicMaker();
    b = a.(durations: 1/4 ! 4, divisions: #[[1,1,1,1,1]], mask: #[2,4,-1], pitches: "c' d' ef' f'");
    b.doLogicalTies({ |each| each.cs.postln }, pitched: true);


    • Example 3

    Iterate non-pitched logicalTies

    a = FoscMusicMaker();
    b = a.(durations: 1/4 ! 4, divisions: #[[1,1,1,1,1]], mask: #[2,4,-1], pitches: "c' d' ef' f'");
    b.doLogicalTies({ |each| each.cs.postln }, pitched: false);
    -------------------------------------------------------------------------------------------------------- */
    doLogicalTies { |function, pitched, graceNotes=false|
        var iterator;
        Fosc.prCheckIsIterable(this, thisMethod);
        iterator = FoscIteration(this).logicalTies(pitched, graceNotes);
        iterator.do(function);
    }
    /* --------------------------------------------------------------------------------------------------------
    • doRuns


    • Example 1

    Attach slurs to each run.

    a = FoscMusicMaker();
    b = a.(durations: 1/4 ! 4, divisions: #[[1,1,1,1]], mask: #[2,2,-1]);
    b.doRuns { |each| if (each.size > 1) { each.slur } };
    b.show;
    -------------------------------------------------------------------------------------------------------- */
    doRuns { |function|
        Fosc.prCheckIsIterable(this, thisMethod);
        ^this.selectRuns.do(function);
    }
    /* --------------------------------------------------------------------------------------------------------
    • doTimeline


    • Example 1

    Iterate all leaves

    a = FoscStaff(FoscLeafMaker().((60..67), [1/8]));
    b = FoscStaff(FoscLeafMaker().((60..63), [1/4]));
    c = FoscScore([a, b]);
    c.doTimeline { |each, i| each.attach(FoscMarkup(i.asString, 'above')) };
    c.show;


    • Example 2

    Iterate pitched leaves

    a = FoscStaff(FoscLeafMaker().([60,61,nil,63,nil,nil,65], [1/8]));
    b = FoscStaff(FoscLeafMaker().((60..63), [1/4]));
    c = FoscScore([a, b]);
    c.doTimeline({ |each, i| each.attach(FoscMarkup(i, 'above')) }, pitched: true);
    c.show;


    • Example 3

    Iterate non-pitched leaves

    a = FoscStaff(FoscLeafMaker().([60,61,nil,63,nil,nil,65], [1/8]));
    b = FoscStaff(FoscLeafMaker().((60..63), [1/4]));
    c = FoscScore([a, b]);
    c.doTimeline({ |each, i| each.attach(FoscMarkup(i, 'above')) }, pitched: false);
    c.show;
    -------------------------------------------------------------------------------------------------------- */
    doTimeline { |function, prototype, pitched|
        Fosc.prCheckIsIterable(this, thisMethod);
        FoscIteration(this).timeline(prototype, pitched).do(function);
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
    doTimelineByLogicalTies { |function, pitched|
        Fosc.prCheckIsIterable(this, thisMethod);
        FoscIteration(this).timelineByLogicalTies(pitched).do(function);
    }
    /* --------------------------------------------------------------------------------------------------------
    • leafAt

    • selection

    a = FoscSelection([FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    a.leafAt(1).str;

    • selection: pitched

    a = FoscSelection([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    a.leafAt(0).str;

    • container

    a = FoscStaff([FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    a.leafAt(1).str;

    • container: pitched

    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    a.leafAt(0, pitched: true).str;
    -------------------------------------------------------------------------------------------------------- */
    leafAt { |index|
        this.doLeaves { |each, i| if (i == index) { ^each } };
        ^nil;
    }
    /* --------------------------------------------------------------------------------------------------------
    • selectComponents

    
    • Example 1

    Select all components

    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = a.selectComponents;
    b.do { |each| each.cs.postln };

    
    • Example 2

    Select notes

    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = a.selectComponents(prototype: FoscNote);
    b.do { |each| each.str.postln };

    
    • Example 3

    Select notes and rests

    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = a.selectComponents(prototype: [FoscNote, FoscRest]);
    b.do { |each| each.str.postln };

    
    • Example 4

    Select notes and rests in reverse order

    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = a.selectComponents(prototype: [FoscNote, FoscRest], reverse: true);
    b.do { |each| each.str.postln };
    -------------------------------------------------------------------------------------------------------- */
    selectComponents { |prototype, exclude, graceNotes=false, reverse=false|
        Fosc.prCheckIsIterable(this, thisMethod);

        ^FoscSelection(this).components(
            prototype: prototype,
            exclude: exclude,
            graceNotes: graceNotes,
            reverse: reverse
        );
    }
    /* --------------------------------------------------------------------------------------------------------
    • selectLeaves


    • Example 1

    Select all leaves

    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = a.selectLeaves;
    b.do { |each| each.str.postln };


    • Example 2

    Select pitched leaves

    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = a.selectLeaves(pitched: true);
    b.do { |each| each.str.postln };


    • Example 3

    Select non-pitched leaves

    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = a.selectLeaves(pitched: false);
    b.do { |each| each.str.postln };
    -------------------------------------------------------------------------------------------------------- */
    selectLeaves { |prototype, pitched, graceNotes=false|
        Fosc.prCheckIsIterable(this, thisMethod);
        ^FoscSelection(this).leaves(prototype, pitched, graceNotes);
    }
    /* --------------------------------------------------------------------------------------------------------
    • selectLogicalTies

    Selects logical ties.

    Returns new selection.

    !!!TODO: not working on selections, use FoscContainer


    •  Example 1

    Select all logicalTies

    a = FoscMusicMaker();
    b = a.(durations: 1/4 ! 4, divisions: #[[1,1,1,1,1]], mask: #[2,4,-1], pitches: "c' d' ef' f'");
    b.selectLogicalTies.do { |each| each.cs.postln };


    • Example 2

    Select pitched logicalTies

    b = a.selectLogicalTies(pitched: true);
    b.do { |each| each.items.collect { |each| each.cs }.postln };


    • Example 3

    Select non-pitched logicalTies

    b = a.selectLogicalTies(pitched: false);
    b.do { |each| each.items.collect { |each| each.cs }.postln };
    -------------------------------------------------------------------------------------------------------- */
    selectLogicalTies { |pitched, graceNotes=false|
        Fosc.prCheckIsIterable(this, thisMethod);
        ^FoscSelection(this).logicalTies(pitched, graceNotes);
    }
    /* --------------------------------------------------------------------------------------------------------
    • selectRuns

    Select runs.


    • Example 1

    Attach slurs to each run.

    a = FoscMusicMaker();
    b = a.(durations: 1/4 ! 4, divisions: #[[1,1,1,1]], mask: #[2,2,-1]);
    b.selectRuns.do { |each| if (each.size > 1) { each.slur } };
    b.show;
    -------------------------------------------------------------------------------------------------------- */
    selectRuns { |exclude|
        Fosc.prCheckIsIterable(this, thisMethod);
        ^FoscSelection(this).runs;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: PLAYBACK
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


    • Example 1

    s.boot;
    a = FoscChord([60,64,67], 1/4);
    a.play;


    • Example 2

    a = FoscStaff(FoscLeafMaker().((60..72), [1/8]));
    a[0..].hairpin('pppp < ffff');
    a.play;


    • Example 3

    a = FoscStaff(FoscLeafMaker().((60..63), [1/32]));
    b = FoscStaff(FoscLeafMaker().((67..70), [1/32]));
    c = FoscScore([a, b]);
    c.play;


    • Example 4

    a = FoscMusicMaker();
    b = a.(durations: 1/4 ! 10, divisions: #[[1,1,1,1]], mask: [1,2,3], pitches: (60..63));
    b.play;
    -------------------------------------------------------------------------------------------------------- */
    play {
        if (this.respondsTo('player')) {
            this.instVarPut('player', this.pattern.play);
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • resume
    -------------------------------------------------------------------------------------------------------- */
    resume {
        if (this.respondsTo('player')) { this.player.resume };
    }
    /* --------------------------------------------------------------------------------------------------------
    • stop
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
