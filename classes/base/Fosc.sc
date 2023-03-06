/* ------------------------------------------------------------------------------------------------------------
• Fosc
------------------------------------------------------------------------------------------------------------ */
Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS PROPERTIES: CONFIGURATION
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    classvar >lilypondPath="lilypond";
    classvar <tuning;
    classvar <>xmlVersion="4.0";
    /* --------------------------------------------------------------------------------------------------------
    • *lilypondVersion
    
    Fosc.lilypondVersion;
    -------------------------------------------------------------------------------------------------------- */
    *lilypondVersion {
        var str;
        str = "% --version".format(this.lilypondPath).unixCmdGetStdOut;
        str = str.copyRange(*[str.findRegexp("\\s[0-9]")[0][0]+1, min(str.find("\n"), str.find(" (")?99) - 1]);
        ^str;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *lilypondPath

    Fosc.lilypondPath;
    -------------------------------------------------------------------------------------------------------- */ 
    *lilypondPath {
        if (lilypondPath.isNil || { File.exists(lilypondPath).not }) {
            error("LilyPond executable not found at: %.".format(lilypondPath));
            ^nil;
        } {
            ^lilypondPath;  
        };
    }
    /* --------------------------------------------------------------------------------------------------------
    • *lilypondPath_

    The executable path should be set in the user startup file, e.g.:

    Fosc.lilypondPath = "/Applications/LilyPond.app/Contents/Resources/bin/lilypond";
    -------------------------------------------------------------------------------------------------------- */ 
    /* --------------------------------------------------------------------------------------------------------
    • *outputDirectory

    Fosc.outputDirectory;
    -------------------------------------------------------------------------------------------------------- */
    *outputDirectory {
        var dir;
        dir = "%/fosc-output".format(Platform.userConfigDir);
        if (File.exists(dir).not) { File.mkdir(dir) };
        ^dir;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *rootDirectory

    Fosc.rootDirectory;
    -------------------------------------------------------------------------------------------------------- */
    *rootDirectory {
        ^Fosc.filenameSymbol.asString.dirname.dirname.dirname;
    }
    /* --------------------------------------------------------------------------------------------------------
    • *stylesheetDirectory

    Fosc.stylesheetDirectory;
    -------------------------------------------------------------------------------------------------------- */
    *stylesheetDirectory {
        ^"%/stylesheets".format(this.rootDirectory);
    }
    /* --------------------------------------------------------------------------------------------------------
    • *tuning_

    Fosc.tuning_('et72');
    Fosc.tuning.name;
    FoscPitchManager.tuning.name;

    a = FoscNote("cfts'", 1/4);
    a.show;
    -------------------------------------------------------------------------------------------------------- */
    *tuning_ { |ltuning|
        case 
        { ltuning.isString } {
            ltuning = ltuning.asSymbol;
            ltuning = FoscTuning.perform(ltuning);
        }
        { ltuning.isKindOf(Symbol) } {
            ltuning = FoscTuning.perform(ltuning);
        }
        { ltuning.isKindOf(FoscTuning) } {
            // pass
        }
        { ltuning.isNil } {
            // pass
        }
        {
            ^throw("Bad argument for %:%: %.".format(this.species, thisMethod.name, tuning));
        };

        tuning = ltuning;
        
        FoscPitchManager.tuning = tuning;
    }
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC INSTANCE METHODS: SPECIAL METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • ==
	-------------------------------------------------------------------------------------------------------- */
	/* --------------------------------------------------------------------------------------------------------
    • !-
	-------------------------------------------------------------------------------------------------------- */
	!= { |object|
		^(this == object).not;
	}
   	/* --------------------------------------------------------------------------------------------------------
    • >
	-------------------------------------------------------------------------------------------------------- */
	/* --------------------------------------------------------------------------------------------------------
    • >=
	-------------------------------------------------------------------------------------------------------- */
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
    • add
	-------------------------------------------------------------------------------------------------------- */
	+ { |object|
		^this.add(object);
	}
	/* --------------------------------------------------------------------------------------------------------
    • concat
	-------------------------------------------------------------------------------------------------------- */
	++ { |object|
		^this.concat(object);
	}
	/* --------------------------------------------------------------------------------------------------------
    • div
	-------------------------------------------------------------------------------------------------------- */
	/ { |object|
		^this.div(object);
	}
	/* --------------------------------------------------------------------------------------------------------
    • dup
	-------------------------------------------------------------------------------------------------------- */
	! { |object|
		^this.dup(object);
	}
	/* --------------------------------------------------------------------------------------------------------
    • mul
	-------------------------------------------------------------------------------------------------------- */
	* { |object|
		^this.mul(object);
	}
	/* --------------------------------------------------------------------------------------------------------
    • sub
	-------------------------------------------------------------------------------------------------------- */
	- { |object|
		^this.sub(object);
	}
	// SET OPERATIONS
	/* --------------------------------------------------------------------------------------------------------
    • difference
    !!!TODO: override lower in the class hierarchy
	-------------------------------------------------------------------------------------------------------- */
	// - { |object|
 	//        ^this.difference(object)
 	//    }
    /* --------------------------------------------------------------------------------------------------------
    • difference
	-------------------------------------------------------------------------------------------------------- */
    & { |object|
        ^this.sect(object);
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
    • lilypond
    
    Formats lilypond string.

    Returns string.

    a = FoscNote(60, 1/4);
    a.lilypond;
    -------------------------------------------------------------------------------------------------------- */
    lilypond {
        if (this.respondsTo('prGetLilypondFormat').not) {
            ^throw("% does not respond to 'lilypond'.".format(this));
        };

        ^this.prGetLilypondFormat;
    }
    /* --------------------------------------------------------------------------------------------------------
    • format

    !!!TODO: DEPRECATE
    -------------------------------------------------------------------------------------------------------- */
    format {
        ^this.lilypond;
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
    • parentage

    a = FoscNote(60, 1/4);
    b = FoscVoice([a]);
    p = a.parentage;
    p.components;
    -------------------------------------------------------------------------------------------------------- */
    parentage { |graceNotes=false|
        ^FoscParentage(this, graceNotes: graceNotes);
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

    Writes LilyPond input file and output PDF to output directory.

    Opens output PDF.


    • Example 1

    a = FoscNote(60, 1/4);
    a.show;


    • Example 2

    a = FoscNote(60, 1/4);
    a.show(staffSize: 12);
    -------------------------------------------------------------------------------------------------------- */
    show { |paperSize, staffSize, includes|
        var illustrateEnvir, path;
        
        if (this.respondsTo('illustrate').not) {
            ^throw("% does not respond to 'illustrate' and can't be shown.".format(this));
        };

        if (includes.notNil && { includes.isSequenceableCollection.not }) { includes = [includes] };
        illustrateEnvir = (paperSize: paperSize, staffSize: staffSize, includes: includes);
        path = this.write.asPDF(illustrateEnvir: illustrateEnvir);
        FoscIOManager.openFile(path);
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // !!! TODO: move write methods into FoscComponent
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • write
	-------------------------------------------------------------------------------------------------------- */
	write {
		^FoscWriteManager(this);
	}
    /* --------------------------------------------------------------------------------------------------------
    • writeLY

    • Example 1

    a = FoscNote(60, 1/4);
    b = a.writeLY(staffSize: 10);
    openOS(b);
    -------------------------------------------------------------------------------------------------------- */
    writeLY { |path, paperSize, staffSize, includes|
        var illustrateEnvir;
        illustrateEnvir = (paperSize: paperSize, staffSize: staffSize, includes: includes);
        ^this.write.asLY(path, illustrateEnvir: illustrateEnvir);
    }
    /* --------------------------------------------------------------------------------------------------------
    • writeMIDI
    -------------------------------------------------------------------------------------------------------- */
    writeMIDI { |path|
        ^this.write.asMIDI(path);
    }
    /* --------------------------------------------------------------------------------------------------------
    • writePDF

     • Example 1

    a = FoscNote(60, 1/4);
    b = a.writePDF(staffSize: 10);
    openOS(b);
    -------------------------------------------------------------------------------------------------------- */
    writePDF { |path, paperSize, staffSize, includes, clean=false|
        var illustrateEnvir;
        illustrateEnvir = (paperSize: paperSize, staffSize: staffSize, includes: includes);
        ^this.write.asPDF(path, illustrateEnvir: illustrateEnvir, clean: clean);
    }
    /* --------------------------------------------------------------------------------------------------------
    • writePNG

    • Example 1

    a = FoscNote(60, 1/4);
    b = a.writePNG(resolution: 300);
    openOS(b);
    -------------------------------------------------------------------------------------------------------- */
    writePNG { |path, staffSize, includes, resolution=300, clean=false|
        var illustrateEnvir;
        illustrateEnvir = (staffSize: staffSize, includes: includes);
        ^this.write.asPNG(path, illustrateEnvir: illustrateEnvir, resolution: resolution, clean: clean);
    }
    /* --------------------------------------------------------------------------------------------------------
    • writeSVG

    • Example 1

    a = FoscNote(60, 1/4);
    b = a.writeSVG;
    openOS(b);
    -------------------------------------------------------------------------------------------------------- */
    writeSVG { |path, staffSize, includes, clean=false|
        var illustrateEnvir;
        illustrateEnvir = (staffSize: staffSize, includes: includes);
        ^this.write.asSVG(path, illustrateEnvir: illustrateEnvir, clean: clean);
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
    doComponents { |function, type, exclude, doNotIterateGraceContainers=false, graceNotes=false,
        reverse=false|
        
        Fosc.prCheckIsIterable(this, thisMethod);        
        
        FoscIteration(this).components(
            type, exclude, doNotIterateGraceContainers, graceNotes, reverse
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
    doLeaves { |function, type, pitched, graceNotes=false|
        Fosc.prCheckIsIterable(this, thisMethod);
        FoscIteration(this).leaves(type, pitched, graceNotes).do(function);
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
    doTimeline { |function, type, pitched|
        Fosc.prCheckIsIterable(this, thisMethod);
        FoscIteration(this).timeline(type, pitched).do(function);
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
    b = a.selectComponents(type: FoscNote);
    b.do { |each| each.str.postln };

    
    • Example 3

    Select notes and rests

    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = a.selectComponents(type: [FoscNote, FoscRest]);
    b.do { |each| each.str.postln };

    
    • Example 4

    Select notes and rests in reverse order

    a = FoscStaff([FoscRest(1/4), FoscNote(60, 1/4), FoscNote(62, 1/4)]);
    b = a.selectComponents(type: [FoscNote, FoscRest], reverse: true);
    b.do { |each| each.str.postln };
    -------------------------------------------------------------------------------------------------------- */
    selectComponents { |type, exclude, graceNotes=false, reverse=false|
        Fosc.prCheckIsIterable(this, thisMethod);

        ^FoscSelection(this).components(
            type: type,
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
    selectLeaves { |type, pitched, graceNotes=false|
        Fosc.prCheckIsIterable(this, thisMethod);
        ^FoscSelection(this).leaves(type, pitched, graceNotes);
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
    selectRuns {
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
    // PRIVATE CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *prCheckIsIterable
    -------------------------------------------------------------------------------------------------------- */
    *prCheckIsIterable { |object, method|
        var type, isIterable;
        
        type = [FoscComponent, FoscSelection, SequenceableCollection];
        isIterable = type.any { |type| object.isKindOf(type) };
        
        if (isIterable.not) {
            ^throw("%: receiver is not iterable: %.".format(method.name, object));
        };
    }
}
