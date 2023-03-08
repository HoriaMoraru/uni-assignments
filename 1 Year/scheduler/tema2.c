#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>
#include <math.h>

/* numar maxim elemente coada / stiva */
#define MAX 256 

typedef struct task
{
    int ID;
    int thread;
    unsigned int priority;
    unsigned int exec_time;
}task;

typedef struct queue
{
	task *data;
	struct queue *next;
}queue;

typedef struct stack
{
	int thread;
	struct stack *next;
}stack;


int empty_queue (queue *q)
{
	if( q == NULL )
		return 1;
	else
		return 0;
}

int empty_stack (stack *top)
{
	if( top == NULL )
		return 1;
	else
		return 0;
}

queue *new_queue (task *t)
{
	queue *aux = (queue*)malloc(sizeof(queue));

    task *tmp = (task*)malloc(sizeof(task));

	tmp->ID = t->ID;
    tmp->thread = t->thread;
    tmp->priority = t->priority;
    tmp->exec_time = t->exec_time;
    aux->data = tmp;
	aux->next = NULL;

	return aux;
}

queue *add_queue (queue *q, task* t)
{
	queue *aux;

	if (empty_queue(q) == 1)
		return new_queue(t);

    if (q->data->priority < t->priority)
    {
        aux = new_queue(t);
        aux->next = q;

        return aux;
    }
    else if(q->data->priority == t->priority)
    {
        if (q->data->exec_time > t->exec_time)
        {
            aux = new_queue(t);
            aux->next = q;

            return aux;
        }
        else if(q->data->exec_time == t->exec_time)
        {
            if(q->data->ID > q->data->ID)
            {
                aux = new_queue(t);
                aux->next = q;

                return aux;
            }
        }
    }

	aux = q;

	while (aux->next != NULL)
    {
        if(aux->next->data->priority > t->priority)
        // merg mai departe
		    aux = aux->next;
        else if (aux->next->data->priority < t->priority)
        {
            // insereaza
            queue *tmp = aux->next;
            aux->next = new_queue(t);
            aux->next->next = tmp;

            return q;
        }
        else
        {
            if(aux->next->data->exec_time < t->exec_time)
                aux = aux->next;
            else if (aux->next->data->exec_time > t->exec_time)
            {
                queue *tmp = aux->next;
                aux->next = new_queue(t);
                aux->next->next = tmp;

            return q;  
            }
            else
            {
                if(aux->next->data->ID < t->ID)
                    aux = aux->next;
                else
                {
                    queue *tmp = aux->next;
                    aux->next = new_queue(t);
                    aux->next->next = tmp;

                    return q; 
                }
            }
            
        }
        
    }
	aux->next = new_queue(t);

	return q;
}

queue *delete_queue (queue *q)
{
	queue *aux;

	if(empty_queue(q) == 1)
		return NULL;

	aux = q;
	q = q->next;
	free(aux);

	return q;
}

stack *new_stack (int id)
{
	stack *aux = (stack*)malloc(sizeof(stack));

    aux->thread = id;
	aux->next = NULL;
	return aux;
}

stack *push (stack *top, int id)
{   
	stack *aux;
	
	if(empty_stack(top))
		return new_stack(id);

	aux = new_stack(id);
	aux->next = top;

	return aux;
}

stack *pop (stack *top)
{
	stack *aux;
	
	if(empty_stack(top))
		return NULL;

	aux = top;
	top = top->next;
	free(aux);

	return top;
}

queue *print_waiting (queue *q, FILE *f)
{   
    fprintf(f, "%s", "====== Waiting queue =======\n");

    fprintf(f, "[");
    while(q != NULL)
    {
        if (q->next == NULL)
            fprintf(f, "(%d: priority = %d, remaining_time = %d)", q->data->ID, q->data->priority, q->data->exec_time);
        else
            fprintf(f, "(%d: priority = %d, remaining_time = %d),\n", q->data->ID, q->data->priority, q->data->exec_time);
        q = q->next;
    }

    fprintf(f, "]\n");
}

queue *print_running (queue *q, FILE *f)
{
    if(q == NULL)
    {   
        fprintf(f, "====== Running in parallel =======\n");
        fprintf(f, "[]\n");
    }
}

queue *print_finished (queue *q, FILE *f)
{
    if(q == NULL)
    {   
        fprintf(f, "====== Finished queue =======\n");
        fprintf(f, "[]\n");
    }
}

queue *add_tasks (queue *q, int n, int exec_time_task, int prio, int *num_task, FILE *f)
{   
    int i;
    for(i = 0; i < n; i++)
    {   
        task *t = (task*)malloc(sizeof(task));
        t->exec_time = exec_time_task;
        t->priority = prio;
        t->ID = *num_task;
        
        q = add_queue(q, t);

        (*num_task)++;
        fprintf(f ,"Task created successfully : ID %d.\n", t->ID);
    }

    return q;
}

void get_task (queue *q, int id, FILE *f)
{   
    while(q != NULL)
    {   
        if(q->data->ID == id)
        {    
            fprintf(f, "Task %d is waiting (remaining_time = %d).\n", q->data->ID, q->data->exec_time);
            return;
        }
        q = q->next;
    }

    fprintf(f, "Task %d not found.\n", id);
}

stack *get_thread (stack *s, int id, FILE *f)
{   
    while(s != NULL)
    {   
        if(s->thread == id)
            fprintf(f, "Thread %d is idle.\n", id);
        
        s = s->next;    
    }
}

int main(int argc, char **argv)
{
    FILE *f_in, *f_out;

    f_in = fopen(argv[1], "r");
	f_out = fopen(argv[2], "w");

    int Q, C, N;
    int num_task = 1;
    int i;

    queue *WAITING = NULL;
    queue *RUNNING = NULL;
    queue *FINISHED = NULL;

    stack *THREADS = NULL;

    char * buffer = malloc(MAX * sizeof(char));

    fgets(buffer, MAX, f_in);
    Q = atoi(buffer);
    fgets(buffer, MAX, f_in);
    C = atoi(buffer);

    N = C * 2;
    
    for(i = N-1; i >= 0; i--)
        THREADS = push(THREADS, i);

    while(fgets(buffer, MAX, f_in) != NULL)
    {   
        char * command = strtok(buffer, " ");

        if(strcmp(command, "add_tasks") == 0)
        {
           int n = atoi(strtok(NULL, " "));
           int exec_time_task = atoi(strtok(NULL, " "));
           int prio = atoi(strtok(NULL, " "));
           WAITING = add_tasks(WAITING, n, exec_time_task, prio, &num_task, f_out);
        }

        if(strcmp(command, "print") == 0)
        {   
            char * type = strtok(NULL, " \n");
            if(strcmp(type, "waiting") == 0)
            {   
                print_waiting(WAITING, f_out);
            }

            else if(strcmp(type, "running") == 0)
            {
                print_running(RUNNING, f_out);
            }

            else if(strcmp(type, "finished") == 0)
            {
                print_finished(FINISHED, f_out);
            }
        }

        if(strcmp(command, "get_task") == 0)
        {
            int get_id = atoi(strtok(NULL, " "));
            get_task(WAITING, get_id, f_out);
        }

        if(strcmp(command, "get_thread") == 0)
        {
            int get_id = atoi(strtok(NULL, " "));
            get_thread(THREADS, get_id, f_out);
        }
    }

    return 0;
}