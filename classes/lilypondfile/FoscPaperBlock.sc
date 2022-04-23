/* ------------------------------------------------------------------------------------------------------------
• FoscPaperBlock

A LilyPond file paper block.

a = FoscBlock(name: 'paper');
a.leftMargin = FoscLilyPondDimension(2, 'cm');
a.rightMargin = FoscLilyPondDimension(2, 'cm');
a.format;

\paper {
    left-margin = 2\cm
    right-margin = 2\cm
}


a = FoscPaperBlock();
a.name;
a.leftMargin_("Missa sexti tonus");
a.rightMargin_("Josquin");
a.format;

%% Fixed vertical spacing
top-margin-default = 5\mm     % scaled to paper-size
bottom-margin-default = 6\mm  % scaled to paper-size
ragged-bottom = ##f
ragged-last-bottom = ##t  % best for shorter scores

%% Flexible vertical spacing
%% Note: these are not scaled; they are in staff-spaces.
system-system-spacing = #'((basic-distance . 12) (minimum-distance . 8) (padding . 1) (stretchability . 60))
score-system-spacing = #'((basic-distance . 14) (minimum-distance . 8) (padding . 1) (stretchability . 120))
markup-system-spacing = #'((basic-distance . 5) (padding . 0.5) (stretchability . 30))
score-markup-spacing = #'((basic-distance . 12) (padding . 0.5) (stretchability . 60))
markup-markup-spacing = #'((basic-distance . 1) (padding . 0.5))
top-system-spacing = #'((basic-distance . 1) (minimum-distance . 0) (padding . 1))
top-markup-spacing = #'((basic-distance . 0) (minimum-distance . 0) (padding . 1))
last-bottom-spacing = #'((basic-distance . 1) (minimum-distance . 0) (padding . 1) (stretchability . 30))

%% Widths and (horizontal) margins
left-margin-default = 10\mm   % scaled to paper-size
right-margin-default = 10\mm  % scaled to paper-size
check-consistency = ##t

%% Two-sided mode
two-sided = ##f
inner-margin-default = 10\mm   % scaled to paper-size
outer-margin-default = 20\mm   % scaled to paper-size
binding-offset-default = 0\mm  % scaled to paper-size

%% Indents
indent-default = 15\mm       % scaled to paper-size
short-indent-default = 0\mm  % scaled to paper-size

%% Page breaking
blank-after-score-page-penalty = 2
blank-last-page-penalty = 0
blank-page-penalty = 5
page-breaking = #ly:optimal-breaking

%% Footnotes
footnote-separator-markup = \markup \fill-line { \override #'(span-factor . 1/2) \draw-hline }
footnote-padding = 0.5\mm
footnote-footer-padding = 0.5\mm
footnote-number-raise = 0.5\mm
footnote-numbering-function = #numbered-footnotes
reset-footnotes-on-new-page = ##t

%% Page numbering
first-page-number = #1
print-first-page-number = ##f
print-page-number = ##t
page-number-type = #'arabic
------------------------------------------------------------------------------------------------------------ */
FoscPaperBlock : FoscBlock {
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	// INIT
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	*new {
		^super.new('paper');
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
    // properties -- _ly/paper-defaults.init.ly
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    leftMargin_ { |string|
        var markup, key;
        markup = FoscMarkup(string);
        vars['left-margin'] = markup;
    }
    rightMargin_ { |string|
        var markup, key;
        markup = FoscMarkup(string);
        vars['right-margin'] = markup;
    }
}