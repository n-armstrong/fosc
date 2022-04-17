#(ly:set-option 'relative-includes #t)
	
\include "default.ily"

#(set-global-staff-size 15)
#(set-default-paper-size "a4" 'portrait)

\header {
    tagline = ##f
}

\layout {
    \context {
       	\Staff
			\remove Bar_engraver
			\remove Time_signature_engraver
			\accidentalStyle dodecaphonic
    }
    \context {
    	\Voice
			\remove Stem_engraver
    }
}


