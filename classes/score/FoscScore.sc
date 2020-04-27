/* ------------------------------------------------------------------------------------------------------------
• FoscScore
------------------------------------------------------------------------------------------------------------ */
FoscScore : FoscContext {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	var <defaultlilypondType='Staff';
	*new { |music, lilypondType, name, tag, playbackManager|
		^super.new(music, lilypondType='Score', true, name, tag, playbackManager);
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// PUBLIC INSTANCE METHODS
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	/* --------------------------------------------------------------------------------------------------------
    • addFinalBarLine

    a = FoscScoreSegment.read(WTGO, 'A1');
    a.show;
    -------------------------------------------------------------------------------------------------------- */
	addFinalBarLine { |abbreviation="|.", toEachVoice=false|
		var barLine, lastLeaf;
        if (abbreviation.isKindOf(FoscBarLine)) { abbreviation = abbreviation.abbreviation };
		barLine = FoscBarLine(abbreviation);
		if (toEachVoice.not) {
			lastLeaf = select(this).leaves.last;
			lastLeaf.attach(barLine);
		} {
			FoscIteration(this).components(prototype: FoscVoice).do { |voice, i|
				lastLeaf = select(voice).leaves.last;
				lastLeaf.attach(barLine);
			};
		};
		^barLine;
	}
	/* --------------------------------------------------------------------------------------------------------
    • addFinalMarkup

    !!!TODO: use hidden skip voice for more accurate horizontal spacing

    a = FoscScore([FoscStaff(FoscLeafMaker().(#[60,62,64,65], 1/4))]);
    m = FoscMarkup("July 2010 - May 2011", direction: 'down');
    m = m.italic;
    a.addFinalMarkup(m, extraOffset: #[0.5, -2]);
    a.show;

    a = FoscScoreSegment.read(Threads, 'A1');
    a.score.addFinalMarkup(FoscMarkup.musicGlyph('scripts.ufermata'), extraOffset: #[55, 0]);
    a.show;
	-------------------------------------------------------------------------------------------------------- */
	addFinalMarkup { |markup, extraOffset|
        var selection, lastLeaf, grobProxy;
        // selection = FoscSelection(this).components(prototype: FoscContext); // top context in score
        // selection.do { |e| e.postln };
        // "selection: ".post; selection.items[0].postln;
        // lastLeaf = FoscSelection(selection.items[1]).leaves.last; //!!!TODO: update to: selection.leafAt(-1)
        selection = FoscSelection(this);
        lastLeaf = selection.leaves.last;
        markup = markup.copy;
        lastLeaf.attach(markup);
        if (extraOffset.notNil) {
            if (lastLeaf.isKindOf(FoscMultimeasureRest)) {
                grobProxy = override(lastLeaf).multiMeasureRestText;
            } {
                grobProxy = override(lastLeaf).textScript;
            };
            grobProxy.extraOffset = extraOffset;
        };
        ^markup;
	}
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *makePianoScore

    Makes piano score from leaves.

    Returns score.
        
    x = [48, 55, 58, 63, 69];
    y = Array.fill(12, { |i| FoscChord(x + i, FoscDuration(1, 4)) });
    b = FoscScore.makePianoScore(y, isSketch: true);
    override(b).stem.stencil_(false);
    b.show;
    -------------------------------------------------------------------------------------------------------- */
    *makePianoScore { |leaves, lowestTreblePitch, isSketch=false|
        var trebleStaff, bassStaff, staffGroup, score, trebleChord, bassChord;
        leaves = leaves ? [];
        if (lowestTreblePitch.isNil) { lowestTreblePitch = FoscPitch(60) };
        trebleStaff = FoscStaff([]);
        trebleStaff.name_('Treble Staff');
        bassStaff = FoscStaff([]);
        bassStaff.name_('Bass Staff');
        staffGroup = FoscStaffGroup([trebleStaff, bassStaff]);
        staffGroup.lilypondType_('PianoStaff');
        score = FoscScore([]);
        score.add(staffGroup);
        leaves.do { |leaf|
            # trebleChord, bassChord = leaf.prDivide(lowestTreblePitch);
            trebleStaff.add(trebleChord);
            bassStaff.add(bassChord);
        };
        if (trebleStaff.size > 0) { trebleStaff[0].attach(FoscClef('treble')) };
        if (bassStaff.size > 0) { bassStaff[0].attach(FoscClef('bass')) };
        if (isSketch) {
            override(score).rest.stencil_(false); //!!! not in abjad
            override(score).timeSignature.stencil_(false);
            override(score).barNumber.transparent_(true);
            override(score).barLine.stencil_(false);
            override(score).spanBar.stencil_(false);
        };
        ^score;
    }
}
