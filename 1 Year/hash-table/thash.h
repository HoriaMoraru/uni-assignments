#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <stdarg.h>
#include <time.h>
#include "tlg.h"

#ifndef _TAB_HASH_
#define _TAB_HASH_

typedef int (*TFElem)(void*);     /* functie prelucrare element */
typedef int (*HashCompare)(void*, void*); /* functie de comparare doua elemente */
typedef void (*TF)(void*);     /* functie afisare/eliberare un element */
typedef int (*HashFunction)(void*);

typedef struct
{
    size_t M;
    HashFunction fh;
    list** v;
} hash;

/* functii tabela hash */
hash* InitHash(size_t M, HashFunction fh);
void Distrhash(hash**aa, TF fe);
void Afihash(hash*a, TF afiEl);
hash* InsertHash(hash* a, void*ae, HashCompare f);

#endif