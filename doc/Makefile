# Makefile for reports
# SD 2018/19
# Gabriel Figueira, Lívio Costa, Rafael Andrade

SHELL         := /bin/sh -e

.PHONY: all clean
all: relatorio.pdf guiao.pdf

clean:
	rm -f relatorio.pdf guiao.pdf

full: clean all


%.pdf: %.md
	pandoc -f markdown -t latex $< -o $@

%.md:
	$(error "Missing Markdown file: $@")
