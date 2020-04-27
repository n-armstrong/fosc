/* ------------------------------------------------------------------------------------------------------------
• FoscTransposition

Transposition operator.

Object model of twelve-tone transposition operator.

!!!TODO: incomplete implementation (see abjad source file)
!!!TODO: accept (cyclic) array and (possibly) patterns as 'interval' argument

• Example 1

a = FoscTransposition(4);
p = FoscPitch(60);
a.(p).pitchNumber;


• Example 2

a = FoscTransposition(4);
p = [FoscPitch(60), FoscPitch(64), FoscPitch(67)];
a.(p).collect { |each| each.pitchNumber };


• Example 3

Only transpose masked pitches.

a = FoscTransposition(4, mask: FoscPitchClassMask(#[0,7]));
p = [FoscPitch(60), FoscPitch(64), FoscPitch(67)];
a.(p).collect { |each| each.pitchNumber };


• Example 4

a = FoscTransposition(4);
p = FoscPitchSegment([FoscPitch(60), FoscPitch(64), FoscPitch(67)]);
a.(p).str;
------------------------------------------------------------------------------------------------------------ */
FoscTransposition : FoscObject {
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // INIT
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    var <interval, mask, intervalStream;
    *new { |interval=0, mask|
        //!!!TODO: interval = FoscInterval(interval);
        ^super.new.init(interval, mask);
    }
    init { |argInterval, argMask|
        interval = argInterval;
        mask = argMask;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PUBLIC INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    value { |object|
        var result;

        intervalStream = interval.asStream;

        case
        { object.respondsTo('transpose') } {
            result = this.prTranspose(object);
        }
        { object.isSequenceableCollection } {
            result = object.collect { |each| this.prTranspose(each) };
        }
        { object.isKindOf(FoscPitchSegment) } {
            result = FoscPitchSegment(object.collect { |each| this.prTranspose(each) });
        }
        {
            error("%:%: do not know how to transpose: %.".format(this.species, thisMethod.name, object));
        };
        ^result;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PRIVATE INSTANCE METHODS
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* --------------------------------------------------------------------------------------------------------
    • prIsIdentityOperator
    -------------------------------------------------------------------------------------------------------- */
    prIsIdentityOperator {
        if (interval == 0) { ^true };
        ^false;
    }
    /* --------------------------------------------------------------------------------------------------------
    • prTranspose
    -------------------------------------------------------------------------------------------------------- */
    prTranspose { |object|
        var localObject;
        if (mask.notNil) {
            localObject = mask.(object);
            if (localObject.notEmpty) {
                ^localObject[0].transpose(intervalStream.next);
            } {
                ^object;
            };
        };
        ^object.transpose(intervalStream.next);
    }
}
