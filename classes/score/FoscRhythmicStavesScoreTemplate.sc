/* ------------------------------------------------------------------------------------------------------------
• FoscRhythmicStavesScoreTemplate
------------------------------------------------------------------------------------------------------------ */
FoscRhythmicStavesScoreTemplate : FoscScoreTemplate {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <staffCount, <includes, <useRelativeIncludes;
    *new { |staffCount=2|
        ^super.new.init(staffCount);
    }
    init { |argStaffCount|
        staffCount = argStaffCount;
        includes = ["/Users/newton/Library/Application\ Support/SuperCollider/Extensions/classes/fosc/docs/_stylesheets/rhythm-maker-docs.ily"];
        useRelativeIncludes = false;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    value {
        var staves, globalContext, number, name, staff, score;
        
        staves = [];

        // global context
        globalContext = FoscContext(name: 'global', lilypondType: 'GlobalContext');
        staves = staves.add(globalContext);

        staffCount.do { |i|
            number = i + 1;
            name = "Staff_%".format(number);
            staff = FoscStaff(name: name, lilypondType: 'RhythmicStaff');
            // staff.annotate('default_clef', FoscClef('percussion'));
            staves = staves.add(staff);
        };

        score = FoscScore(staves);
        ^score;
    }
}
