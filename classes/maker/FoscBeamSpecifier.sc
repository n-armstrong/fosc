/* ------------------------------------------------------------------------------------------------------------
• FoscBeamSpecifier

Beam specifier.


• Example 1

Beams each division by default.

a = FoscStaff(FoscLeafMaker().(#[60], (1/8 ! 2) ++ (1/16 ! 4) ++ (1/8 ! 4)));
b = FoscStaff(a);
set(a).autoBeaming = false;
b = a.selectLeaves.partitionBySizes(#[4,3,3]);
FoscBeamSpecifier().(b);
a.show;


• Example 2

Beams each division but exclude rests.

a = FoscStaff(FoscLeafMaker().((60 ! 7) ++ [nil] ++ (60 ! 2), (1/8 ! 2) ++ (1/16 ! 4) ++ (1/8 ! 4)));
set(a).autoBeaming = false;
b = a.selectLeaves.partitionBySizes(#[4,6]);
FoscBeamSpecifier(beamEachDivision: true, beamRests: false).(b);
a.show;


• Example 3

Beams each division and include rests.

a = FoscStaff(FoscLeafMaker().((60 ! 7) ++ [nil] ++ (60 ! 2), (1/8 ! 2) ++ (1/16 ! 4) ++ (1/8 ! 4)));
set(a).autoBeaming = false;
b = a.selectLeaves.partitionBySizes(#[4,6]);
FoscBeamSpecifier(beamEachDivision: true, beamRests: true).(b);
a.show;


• Example 4

Beams divisions together but exclude rests.

a = FoscStaff(FoscLeafMaker().((60 ! 7) ++ [nil] ++ (60 ! 2), (1/8 ! 2) ++ (1/16 ! 4) ++ (1/8 ! 4)));
set(a).autoBeaming = false;
b = a.selectLeaves.partitionBySizes(#[4,6]);
FoscBeamSpecifier(beamDivisionsTogether: true, beamRests: false).(b);
a.show;


• Example 5

Beams divisions together and include rests.

a = FoscStaff(FoscLeafMaker().((60 ! 7) ++ [nil] ++ (60 ! 2), (1/8 ! 2) ++ (1/16 ! 4) ++ (1/8 ! 4)));
set(a).autoBeaming = false;
b = a.selectLeaves.partitionBySizes(#[4,6]);
FoscBeamSpecifier(beamDivisionsTogether: true, beamRests: true).(b);
a.show;


• Example 6

Beams rests with stemlets.

a = FoscStaff(FoscLeafMaker().((60 ! 7) ++ [nil] ++ (60 ! 2), (1/8 ! 2) ++ (1/16 ! 4) ++ (1/8 ! 4)));
set(a).autoBeaming = false;
b = a.selectLeaves.partitionBySizes(#[4,6]);
FoscBeamSpecifier(beamRests: true, stemletLength: 2).(b);
a.show;
------------------------------------------------------------------------------------------------------------ */
FoscBeamSpecifier : Fosc {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <beamEachDivision, <beamEachRun, <beamDivisionsTogether, <beamRests, <hideNibs, <stemletLength,
        <useFeatherBeams;
    var <publishStorageFormat=true;
    *new { |beamEachDivision=true, beamEachRun=false, beamDivisionsTogether=false, beamRests=false,
        hideNibs=true, stemletLength, useFeatherBeams=false|

        var beamSpanArgs, msg;
        
        assert(beamEachDivision.isKindOf(Boolean), thisMethod, 'beamEachDivision', beamEachDivision);
        assert(beamEachRun.isKindOf(Boolean), thisMethod, 'beamEachRun', beamEachRun);
        assert(beamDivisionsTogether.isKindOf(Boolean), thisMethod, 'beamDivisionsTogether', beamDivisionsTogether);
        assert(beamRests.isKindOf(Boolean), thisMethod, 'beamRests', beamRests);
        assert(hideNibs.isKindOf(Boolean), thisMethod, 'hideNibs', hideNibs);
        assert(stemletLength.isNumber || stemletLength.isNil, thisMethod, 'stemletLength', stemletLength);
        assert(useFeatherBeams.isKindOf(Boolean), thisMethod, 'useFeatherBeams', useFeatherBeams);

        beamSpanArgs = [beamEachDivision, beamEachRun, beamDivisionsTogether];

        if (beamSpanArgs.select { |bool| bool }.size > 1) {
            msg = "%:%: only one of these arguments should be true:".format(this.species, thisMethod.name);
            msg = msg ++ "\nbeamEachDivision: %".format(beamEachDivision);
            msg = msg ++ "\nbeamEachRun: %".format(beamEachRun);
            msg = msg ++ "\nbeamDivisionsTogether: %".format(beamDivisionsTogether);
            ^throw(msg);
        };
        
        ^super.new.init(beamEachDivision, beamEachRun, beamDivisionsTogether, beamRests, hideNibs,
            stemletLength, useFeatherBeams);
    }
    init { |argBeamEachDivision, argBeamEachRun, argBeamDivisionsTogether, argBeamRests, argHideNibs,
        argStemletLength, argUseFeatherBeams|
        
        beamEachDivision = argBeamEachDivision;
        beamEachRun = argBeamEachRun;
        beamDivisionsTogether = argBeamDivisionsTogether;
        beamRests = argBeamRests;
        hideNibs = argHideNibs;
        stemletLength = argStemletLength;
        useFeatherBeams = argUseFeatherBeams;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE PROPERTIES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • beamDivisionsTogether

    Is true when divisions should beam together. 
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • beamEachDivision

    Is true when specifier beams each division.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • beamRests

    Is true when beams should include rests.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • hideNibs

    Is true when specifier hides nibs.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • stemletLength

    Gets stemlet length.
    -------------------------------------------------------------------------------------------------------- */
    /* --------------------------------------------------------------------------------------------------------
    • useFeatherBeams

    Is true when multiple beams should feather.
    -------------------------------------------------------------------------------------------------------- */
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS: SPECIAL METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • asCompileString

    !!!TODO: not yet implemented

    Gets interpreter representation of beam specifier.
    
    Returns string.
    -------------------------------------------------------------------------------------------------------- */
    // asCompileString {
    //     ^this.notYetImplemented(thisMethod);
    // }
    /* --------------------------------------------------------------------------------------------------------
    • value

    Calls beam specifier on 'selections'.


    a = FoscMusicMaker(beamEachRun: true);
    b = a.(durations: 1/4 ! 4, divisions: #[[1,1,1,1,1]], mask: #[1,2,-1]);
    b.show;
    -------------------------------------------------------------------------------------------------------- */
    value { |selections|
        var beam, durations, duration, components, leaves, runs;
        
        this.prDetachAllBeams(selections);

        case
        { beamDivisionsTogether } {
            durations = [];
            selections.do { |selection|
                if (selection.isKindOf(FoscSelection)) {
                    duration = selection.duration;
                } {
                    duration = selection.prGetDuration;
                };
                durations = durations.add(duration);
            };
            components = [];
            selections.do { |selection|
                case
                { selection.isKindOf(FoscSelection) } {
                    components = components.addAll(selection);
                }
                { selection.isKindOf(FoscTuplet) } {
                    components = components.add(selection);
                }
                {
                    ^throw("%:value: selection is of the wrong type: %.".format(this.species, selection.cs));
                };
            };

            leaves = FoscSelection(components).leaves(graceNotes: false);
            leaves.beam(beamRests: beamRests, durations: durations, stemletLength: stemletLength);
        }
        { beamEachDivision } {
            selections.do { |selection|
                leaves = FoscSelection(selection).leaves(graceNotes: false);
                leaves.beam(beamRests: beamRests, stemletLength: stemletLength);
            };
        }
        //!!!TODO: beamRests, stemletLength, etc.
        { beamEachRun } {
            FoscSelection(selections).doRuns { |run|
                run.beam(durations: durations, beamLoneNotes: false);
            };
        };
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prDetachAllBeams
    -------------------------------------------------------------------------------------------------------- */
    prDetachAllBeams { |selections, graceNotes=false|
        FoscIteration(selections).leaves(graceNotes: graceNotes).do { |leaf|
            leaf.detach(FoscStartBeam);
            leaf.detach(FoscStopBeam);
        };
    }
}
