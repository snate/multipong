default: compile

compile: multipong_report.tex
	pdflatex --shell-escape multipong_report.tex
	pdflatex --shell-escape multipong_report.tex
	pdflatex --shell-escape multipong_report.tex

open:
	open multipong_report.pdf

clean:
	rm -vf *.aux *.log *.lof *.lot *.toc *.pdf *.mp *.mps *.dvi *.out *.bbl *.blg
