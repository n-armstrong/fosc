/* ------------------------------------------------------------------------------------------------------------
• FoscScore
------------------------------------------------------------------------------------------------------------ */
FoscScore : FoscContext {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	var <defaultlilypondType='Staff';
	*new { |music, lilypondType, name|
		^super.new(music, lilypondType='Score', true, name);
	}
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC CLASS METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • *makePianoScore


    !!! TODO: move the formatting parts into a FoscScoreTemplate and call the template implicitly
    !!! TODO: use a smaller staff size?
    !!! TODO: use dodecaphonic accidental spellings


    Makes piano score from leaves.

    Returns score.


    • Example 1

    m = FoscLeafMaker().(#[48,55,58,63,69].mirror, [1/4]);
    b = FoscScore.makePianoScore(m);
    b.show;


    • Example 2 - Show in a single staff by setting 'lowestTreblePitch'.

    m = FoscLeafMaker().(#[48,55,58,63,69].mirror, [1/4]);
    b = FoscScore.makePianoScore(m, lowestTreblePitch: 48);
    b.show;


    • Example 3

    x = #[48,55,58,63,69];
    y = Array.fill(12, { |i| FoscChord(x + i, FoscDuration(1, 4)) });
    b = FoscScore.makePianoScore(y, isSketch: true);
    b.show;
    -------------------------------------------------------------------------------------------------------- */
    *makePianoScore { |leaves, lowestTreblePitch=60, isSketch=true|
        var trebleStaff, bassStaff, staffGroup, score, treblePitches, bassPitches, pitches, writtenDuration;
        var trebleLeaf, bassLeaf;
       
        leaves = leaves ? [];
        lowestTreblePitch = FoscPitch(lowestTreblePitch);
        
        trebleStaff = FoscStaff([]);
        trebleStaff.name_('Treble Staff');
        
        bassStaff = FoscStaff([]);
        bassStaff.name_('Bass Staff');
        
        staffGroup = FoscStaffGroup([trebleStaff, bassStaff]);
        staffGroup.lilypondType_('PianoStaff');
        
        score = FoscScore([]);
        score.add(staffGroup);
        
        leaves.do { |leaf|
            pitches = [];
            treblePitches = [];
            bassPitches = [];
            writtenDuration = leaf.writtenDuration;

            if (leaf.isPitched) {
                if (leaf.isKindOf(FoscChord)) {
                    pitches = leaf.writtenPitches;
                } {
                    pitches = [leaf.writtenPitch];
                };

                pitches.do { |pitch|
                    if (pitch < lowestTreblePitch) {
                        bassPitches = bassPitches.add(pitch);
                    } {
                        treblePitches = treblePitches.add(pitch)
                    };
                };
            };

            if (treblePitches.isEmpty) {
                trebleLeaf = FoscRest(writtenDuration);
            } {
                if (treblePitches.size == 1) {
                    trebleLeaf = FoscNote(treblePitches[0], writtenDuration);
                } {
                    trebleLeaf = FoscChord(treblePitches, writtenDuration);
                };
            };

            if (bassPitches.isEmpty) {
                bassLeaf = FoscRest(writtenDuration);
            } {
                if (bassPitches.size == 1) {
                    bassLeaf = FoscNote(bassPitches[0], writtenDuration);
                } {
                    bassLeaf = FoscChord(bassPitches, writtenDuration);
                };
            };

            trebleStaff.add(trebleLeaf);
            bassStaff.add(bassLeaf);
        };

        if (trebleStaff.selectLeaves(pitched: true).size > 0) {
            trebleStaff[0].attach(FoscClef('treble'));
        } {
            staffGroup.remove(trebleStaff);
        };

        if (bassStaff.selectLeaves(pitched: true).size > 0) {
            bassStaff[0].attach(FoscClef('bass'));
        } {
            staffGroup.remove(bassStaff);
        };
        
        if (isSketch) {
            override(score).barLine.stencil_(false);
            override(score).barNumber.transparent_(true);
            override(score).rest.stencil_(false);
            override(score).spanBar.stencil_(false);
            override(score).stem.stencil_(false);
            override(score).timeSignature.stencil_(false);
            override(score).verticalAxisGroup.staffStaffSpacing_(FoscSpacingVector(1, 1, 1, 1));
        };
        
        ^score;
    }
}
