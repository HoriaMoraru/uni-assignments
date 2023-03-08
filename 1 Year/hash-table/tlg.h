/*-- tlg.h --- LISTA SIMPLU INLANTUITA GENERICA ---*/
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <stdarg.h>
#include <time.h>

#ifndef _LISTA_GENERICA_
#define _LISTA_GENERICA_

typedef struct celula
{
  void* info;           /* adresa informatie */
  struct celula *urm;   /* adresa urmatoarei celule */
} list; /* tipurile Celula, Lista  */

typedef struct 
{
    char*word;
    int freq;
} cuv;

typedef struct
{
    int len;
    list* cuv;
} len;

typedef int (*TFElem)(void*);     /* functie prelucrare element */
typedef int (*TFCmp)(void*, void*); /* functie de comparare doua elemente */
typedef void (*TF)(void*);     /* functie afisare/eliberare un element */

#endif
