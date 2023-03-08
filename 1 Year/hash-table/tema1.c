/* MORARU Horia-Andrei -312CB */
/*-- TEMA 1 --*/
#include <stdio.h>
#include "tlg.h"
#include "thash.h"
#include <string.h>
#include <stdlib.h>


int codHash(void * element)
{
	cuv * cuvant = (cuv *) element;
	char * word = cuvant->word;

    if(*word <= 'Z')
	    return *word - 'A';
    else
        return *word - 'a';   
}

char* PrintWord(void* element)
{
	cuv* cuvant = (cuv*) element;

    char* str = (char *)malloc(30 * sizeof(char));

    sprintf(str, "%s/%d", cuvant->word, cuvant->freq);

    return str;
}

char* PrintList(list* words, int n) {
    char* str = (char *)calloc(100, sizeof(char));
    list* tmp = words;
    while (tmp != NULL)
    {
        if (n == -1 || ((cuv*) tmp->info)->freq <= n)
        {
            strcat(str, PrintWord(tmp->info));
            strcat(str, ", ");
        }
        tmp = tmp->urm;
    }

    int size = strlen(str);
    if (str[size-2] == ',')
        str[size-2] = '\0';

    return str;
}

void PrintWordsLen(hash* h, char c, int n)
{
    int cod;
    
    if (c >= 'a')
        cod = c - 'a';
    else
        cod = c - 'A';

    list* aux = h->v[cod];

    while(aux != NULL)
    {
        if(((len*) aux->info)->len == n)
            printf("(%d:%s)\n", n, PrintList(((len*) aux->info)->cuv, -1));

        aux = aux->urm;
    }
}

char* PrintLength(void* element, int n)
{
    char* str = (char*) calloc(100, sizeof(char));
    len* length = (len*) element;

    if (n == -1)                                                                                                     
        sprintf(str, "(%d:%s)", length->len, PrintList(length->cuv, n));
    else
        sprintf(str, "(%d: %s)", length->len, PrintList(length->cuv, n));

    return str;
}

char* PrintList2(list* words, int n)
{
    char* str = (char *)calloc(100, sizeof(char));

    list* tmp = words;
    while (tmp != NULL)
    {
        char* l = (char*) calloc(100, sizeof(char));
        char* l2 = (char*) calloc(100, sizeof(char));
        strcpy(l, PrintLength(tmp->info, n));
        if (n == -1)
            sprintf(l2, "(%d:)", ((len*) tmp->info)->len);
        else
            sprintf(l2, "(%d: )", ((len*) tmp->info)->len);

        if (strcmp(l, l2) != 0)
           strcat(str, PrintLength(tmp->info, n));
        tmp = tmp->urm;
    }

    return str;
}

void PrintWordsMax(hash* h, int n)
{
    int i = 0;

    for (i = 0; i < h->M; i++)
    {
        if (h->v[i] != NULL)
        {
            char* l = PrintList2(h->v[i], n);
            if (strcmp(l, "") != 0)
            {
                printf("pos%d: ", i);
                printf("%s\n", PrintList2(h->v[i], n));
            }
        }
    }
}

void PrintHash(hash* h) {
    list* l;
    int i = 0;

    for (i = 0; i < h->M; i++)
    {
        if (h->v[i] != NULL)
        {
            printf("pos %d: ", i);
            printf("%s\n", PrintList2(h->v[i], -1));
        }
    }
}

list* RemoveWord(list* lista, void* element) 
{
    list* tmp = lista;

    if (strcmp(((cuv*) lista->info)->word, ((cuv*) element)->word) == 0) {
        lista = lista->urm;

        return lista;
    }

    while (tmp->urm != NULL) {
        if (strcmp(((cuv*) tmp->urm->info)->word, ((cuv*) element)->word) == 0) {
            tmp->urm = tmp->urm->urm;

            return lista;
        }

        tmp = tmp->urm;
    }

    return lista;
}

list* InsertAtIndex(list* lista, void* element, int index)
{
    list* aux = malloc(sizeof(list));
    list* tmp = lista;
    int i = 0;

    if (!aux)
        return 0;

    aux->info = element;
    aux->urm = NULL;

    if (index == -1)
    {
        aux->urm = lista;
        return aux;
    }

    while (i < index)
    {
        i++;
        tmp = tmp->urm;
    }

    aux->urm = tmp->urm;
    tmp->urm = aux;

    return lista;
}

list* InsertLast(list* lista, void* element)
{
    list * aux = (list*) malloc(sizeof(list));
    list * tmp = lista;
    
    cuv *cuvant = (cuv*) element;
   
    aux->info = element;
    aux->urm = NULL;

    while (tmp->urm != NULL){
        tmp = tmp->urm;
    }

    tmp->urm = aux;

    return lista;
}

list* InsertWord(list* lista, void* element)
{
	list* aux = malloc(sizeof(list));
    list* tmp = lista;
    int i = 0;
    cuv* new_word = (cuv*) element;

	if (!aux)
	    return 0;                               

	aux->info = element;
    aux->urm = NULL;

    if (lista == NULL){
        return aux;
    }

    while (tmp != NULL){
        cuv* current_word = (cuv*) tmp->info;

        if (strcmp(current_word->word, new_word->word) == 0){
            ((cuv*)tmp->info)->freq += 1;
            cuv* el = (cuv*) tmp->info;

        
            // scoatem cuvantul
            lista = RemoveWord(lista, element);
       
            lista = InsertWord(lista, (void*) el);

            return lista;
        }

        tmp = tmp->urm;
    }               

    tmp = lista;

    while (tmp != NULL)
    {
        cuv* current_word = (cuv*) tmp->info;

        if (new_word->freq == current_word->freq)
        {
            if (strcmp(new_word->word, current_word->word) > 0)
            {
                // Inserez dupa = inserez la pos + 1
                if (tmp->urm == NULL)
                {
                    aux->urm = tmp->urm;
                    tmp->urm = aux;

                    return lista;
                }
            }
            else
            {
                // Inserez inainte = inserez la pos - 1
                return InsertAtIndex(lista, element, i-1);
            }
        }
        else
        {
            if (new_word->freq < current_word->freq)
            {
                if (tmp->urm == NULL)
                {
                    aux->urm = tmp->urm;
                    tmp->urm = aux;

                    return lista;
                }
            }
            else 
            {
                return InsertAtIndex(lista, element, i-1);
            }
        }
        

        i++;
        tmp = tmp->urm;
    }

    tmp->urm = aux;
	
	return lista;
}

int cmpCuvant(void * e1, void * e2)
{   
    cuv * cuvant1 = (cuv *) e1;
	cuv * cuvant2 = (cuv *) e2;

    if(strlen(cuvant1->word) != strlen(cuvant2->word))
        return 0;

    if(cuvant1->freq != cuvant2->freq)
        return 0;

    if (strcmp(cuvant1->word, cuvant2->word) != 0)
		return 0;
    
    return 1;
    
}

void ReadWords(char* nume_fisier, hash* h)
{
    FILE *f;
    char *line = NULL;
    size_t len = 0;

    f = fopen(nume_fisier, "rt");
    if (f == NULL)
        return;
    
    while(getline(&line, &len, f) != -1)
    {     
        char* word = strtok(line, " .");

        if(strcmp(word, "insert") == 0)
        {
            word = strtok(NULL, " .0123456789,");

            while(word != NULL)
            {
                if (word[strlen(word) - 1] == '\n')
                    word[strlen(word) - 1] = '\0';

                cuv* cuvant = malloc(sizeof(cuv));
                cuvant->word = malloc(30 * sizeof(char));

                if(cuvant == NULL)
                    return;

                strcpy(cuvant->word, word);
                cuvant->freq = 1;

                if(strlen(cuvant->word) >= 3) {
                    h = InsertHash(h, (void*) cuvant, cmpCuvant);
                }

                word = strtok(NULL, " .0123456789,");
            }

        }
        else
        {
            char* a1 = strtok(NULL, " .");
            char* a2 = strtok(NULL, " .");

            if (a1 == NULL)
            {
                PrintHash(h);
            }

            else if (a2 == NULL)
            {
                //printam max 
                int max_freq = atoi(a1);
                PrintWordsMax(h, max_freq);
            }

            else 
            {
                char ch = a1[0];
                int n = atoi(a2);
                PrintWordsLen(h, ch, n);
            }

        }
    }

    fclose(f);
}

hash* InitHash(size_t M, HashFunction fh)
{
    hash* h = (hash*) calloc(sizeof(hash), 1);

    if (!h) {
        printf("eroare alocare hash\n");
        return NULL;
    }

    h->v = (list**) calloc(M, sizeof(list*));

    if(!h->v) {
        printf("eroare alocare vector de pointeri TLG in hash\n");
        //free(h);
        return NULL;
    }

    h->M = M;
    h->fh = fh;
    return h;
}

list* InsertLength(list *l, void* element)
{
    list * aux = (list*) malloc(sizeof(list));
    list * tmp = l;
    len * length = (len*) malloc(sizeof(len));
    length->cuv = NULL;
    int i = 0;

    cuv *cuvant = (cuv*) element;

    length->len = strlen(cuvant->word);
    length->cuv = InsertWord(length->cuv, element);
    aux->info = (void*) length;
    aux->urm = NULL;

    if (l == NULL)
        return aux;

    while (tmp != NULL){
        if (((len*)tmp->info)->len == length->len)
        {   
            ((len*)tmp->info)->cuv = InsertWord(((len*)tmp->info)->cuv, element);
            return l;
        }
        else if (((len*)tmp->info)->len > length->len)
        {
            // insereaza inainte
            return InsertAtIndex(l, (void*)length, i-1);
        }
        else
        {
            // insereaza dupa
            if (tmp->urm == NULL)
            {
                aux->urm = tmp->urm;
                tmp->urm = aux;

                return l;
            }
        }
        
        i += 1;
        tmp = tmp->urm;
    }

    return InsertLast(l, (void*) length);
}   

hash* InsertHash(hash* h, void* element, HashCompare fcmp)
{
    int cod = h->fh(element), rez;
    list* el;

    h->v[cod] = InsertLength(h->v[cod], element); /* reminder: a->v+cod <=> &a->v[cod] */
    return h;
}

int main (int argc, char **argv) 
{                                   

    hash* h = NULL;

    size_t M = (('Z' - 'A') + 1);
    h = (hash*) InitHash(M, codHash);
    if (h == NULL) {
		printf("Tabela hash nu a putut fi generata\n");
        return 0;
	}

    ReadWords(argv[1], h);

  	return 0;

}