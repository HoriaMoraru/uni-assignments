#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>

#define MAX 256 

typedef struct file
{
    char *name;
    struct directory *parent;
    struct file *left;
    struct file *right;
} file;

typedef struct directory
{
    char *name;
    struct directory *parent;
    struct file *files;
    struct directory *directories;
    struct directory *left;
    struct directory *right;
} directory;

directory *new_directory (directory *d, char *file_name)
{
    directory *temp = malloc(sizeof(directory));

    if(temp == NULL)
        return NULL;

    temp->name = malloc(sizeof(char) * (strlen(file_name) + 1));
    strcpy(temp->name, file_name);
    temp->parent = d;
    temp->files = NULL;
    temp->directories = NULL;
    temp->left = NULL;
    temp->right = NULL;

    return temp;

}

file *new_file (directory *d, char *file_name)
{
    file *temp = malloc(sizeof(file));

    if(temp == NULL)
        return NULL;

    temp->name = malloc(sizeof(char) * (strlen(file_name) + 1));
    strcpy(temp->name, file_name);
    temp->parent = d;
    temp->left = NULL;
    temp->right = NULL;

    return temp;

}

void ls_directory (directory *d)
{
    
    if(d == NULL)
    {
        return;
    }

    ls_directory(d->left);
    printf("%s ", d->name);
    ls_directory(d->right);

}

void ls_files (file *f)
{
    if(f == NULL)
    {
        return;
    }

    ls_files(f->left);
    printf("%s ", f->name);
    ls_files(f->right);

}

void touch (directory *d , char* file_name)
{   
    directory *temp = d->directories;

    while(temp != NULL)
    {
        if(strcmp(temp->name, file_name) == 0)
        {
            printf("Directory %s already exists!\n", file_name);
            return;
        }
        else if(strcmp(temp->name, file_name) > 0)
        {
            temp = temp->left;
        }
        else
            temp = temp->right;
    }

    if(d->files == NULL)
        d->files = new_file(d, file_name);
    
    else
    {
        file *current = d->files;
        
        while(current != NULL)
        {
            if(strcmp(current->name, file_name) == 0)
            {
                printf("File %s already exists!\n", file_name);
                return;
            }
            if(strcmp(current->name, file_name) > 0)
            {
                if(current->left == NULL)
                {
                    current->left = new_file(d, file_name);
                    return;
                }
                current = current->left;

            }
            else
            {   
                if(current->right == NULL)
                {
                    current->right = new_file(d, file_name);
                    return;
                }
                current = current->right;
            }
        }   
    }  
}

void mkdir (directory *d , char* file_name)
{
    if(d->directories == NULL)
        d->directories = new_directory(d, file_name);
    
    else
    {
        file *temp = d->files;

        while(temp != NULL)
        {
            if(strcmp(temp->name, file_name) == 0)
            {
                printf("File %s already exists!\n", file_name);
                return;
            }
            if(strcmp(temp->name, file_name) > 0)
            {
                temp = temp->left;
            }
            else
                temp = temp->right;

        }

        directory *current = d->directories;
        
        while(current != NULL)
        {
            if(strcmp(current->name, file_name) == 0)
            {
                printf("Directory %s already exists!\n", file_name);
                return;
            }
            if(strcmp(current->name, file_name) > 0)
            {
                if(current->left == NULL)
                {
                    current->left = new_directory(d, file_name);
                    return;
                }
                current = current->left;

            }
            else
            {   
                if(current->right == NULL)
                {
                    current->right = new_directory(d, file_name);
                    return;
                }
                current = current->right;
            }
        }   
    }  
}

file *min_value_file(file *f)
{
    file * temp = f;

    while(temp && temp->left != NULL)
        temp = temp->left;
    
    return temp;
}

file *rm_file (file *f, char *file_name)
{   

    file *temp = f;

    if(temp == NULL)
    {   
        return temp;
    }

    else if(strcmp(temp->name, file_name) > 0)
        temp->left = rm_file(temp->left, file_name);

    else if(strcmp(temp->name, file_name) < 0)
        temp->right = rm_file(temp->right, file_name);
    
    else
    {
        if(temp->left == NULL) 
        {
            file *aux = temp->right;
            free(temp->name);
            free(temp);
            return aux;
        }

        else if(temp->right == NULL)
        {
            file *aux = temp->left;
            free(temp->name);
            free(temp);
            return aux;
        }

        file *aux = min_value_file(temp->right);

        free(temp->name);
        temp->name = malloc(sizeof(char) * (strlen(aux->name) + 1));

        strcpy(temp->name, aux->name);

        temp->right = rm_file(temp->right, temp->name);

    }

    return temp;
}

directory *min_value_directory(directory *d)
{
    directory * temp = d;

    while(temp && temp->left != NULL)
        temp = temp->left;
    
    return temp;
}

directory *rm_directory (directory *d, char *file_name)
{   

    directory *temp = d;

    if(temp == NULL)
    {   
        return temp;
    }

    else if(strcmp(temp->name, file_name) > 0)
        temp->left = rm_directory(temp->left, file_name);

    else if(strcmp(temp->name, file_name) < 0)
        temp->right = rm_directory(temp->right, file_name);
    
    else
    {
        if(temp->left == NULL) 
        {
            directory *aux = temp->right;
            free(temp->directories);
            free(temp->files);
            free(temp->name);
            free(temp);
            return aux;
        }

        else if(temp->right == NULL)
        {
            directory *aux = temp->left;
            free(temp->directories);
            free(temp->files);
            free(temp->name);
            free(temp);
            return aux;
        }

        directory *aux = min_value_directory(temp->right);

        free(temp->name);
        temp->name = malloc(sizeof(char) * (strlen(aux->name) + 1));

        strcpy(temp->name, aux->name);

        temp->right = rm_directory(temp->right, temp->name);
    }

    return temp;
}

directory *cd (directory *d, char *dir_name)
{
    directory *temp = d->directories;

    if(strcmp(dir_name, "..") == 0)
    {   
        if(strcmp(d->name, "root") == 0)
            return d;

        return d->parent;
    }
    
    while(temp != NULL)
    {   
        if(strcmp(temp->name, dir_name) == 0)
        {
            return temp;
        }
        else if(strcmp(temp->name, dir_name) > 0)
            temp = temp->left;
        else
            temp = temp->right;    
    }

    printf("Directory not found!\n");

    return d;
}

void pwd (directory *d)
{
    directory *temp = d;

    char path[100] = "";

    while(temp != NULL)
    {   
        char aux[100] = "/";
        strcat(aux, temp->name);
        strcat(aux, path);
        strcpy(path, aux);

        temp = temp->parent;
    }

    printf("%s\n", path);
}

int find_file_recursively(directory *d, char* file_name)
{   
    if(d == NULL)
        return 0;

    file *temp = d->files;

    while(temp != NULL)
    {
        if(strcmp(temp->name, file_name) == 0)
        {
            printf("File %s found!\n", file_name);
            pwd(temp->parent);
            return 1;
        }

        if(strcmp(temp->name, file_name) > 0)
            temp = temp->left;
        else
            temp = temp->right; 
    }

    if(find_file_recursively(d->left, file_name) == 1)
        return 1;
    if(find_file_recursively(d->right, file_name) == 1)
        return 1;
    if(find_file_recursively(d->directories, file_name) == 1)
        return 1;

    return 0;
}

int find_directory_recursively(directory *d, char* dir_name)
{   
    if(d == NULL)
        return 0;

    directory *temp = d->directories;

    while(temp != NULL)
    {
        if(strcmp(temp->name, dir_name) == 0)
        {
            printf("Directory %s found!\n", dir_name);
            pwd(temp);
            return 1;
        }

        if(strcmp(temp->name, dir_name) > 0)
            temp = temp->left;
        else
            temp = temp->right; 
    }

    if(find_directory_recursively(d->left, dir_name) == 1)
        return 1;
    if(find_directory_recursively(d->right, dir_name) == 1)
        return 1;
    if(find_directory_recursively(d->directories, dir_name) == 1)
        return 1;

    return 0;
}

void find_file (directory *d, char * file_name)
{   
    directory *temp = d;

    while(strcmp(temp->name, "root") != 0)
    {
        temp = temp->parent;
    }

    if(find_file_recursively(temp, file_name) == 1)
        return;

    printf("File %s not found!\n", file_name);
}

void find_directory (directory *d, char * dir_name)
{   
    directory *temp = d;

    while(strcmp(temp->name, "root") != 0)
    {
        temp = temp->parent;
    }

    if(find_directory_recursively(temp, dir_name) == 1)
        return;

    printf("Directory %s not found!\n", dir_name);
}

void destroy_file (file *f) 	
{
	if (f == NULL) 
        return;

	destroy_file(f->left);     
    destroy_file(f->right);

    free(f->name);
	free(f);             
}

void destroy_directory (directory *d) 	
{
	if (d == NULL)
        return;

	destroy_directory(d->left);     
	destroy_directory(d->right);

    free(d->name);
	free(d);            
}

int main(int argc, char **argv)
{

    directory *root = malloc(sizeof(directory));

    if(root == NULL)
        return -1;

    root->name = malloc(sizeof(char) * (strlen("root") + 1));
    strcpy(root->name, "root");
    root->parent = NULL;
    root->files = NULL;
    root->directories = NULL;
    root->left = NULL;
    root->right = NULL;

    directory *current;
    current = root;

    char * buffer = malloc(MAX * sizeof(char));

    if(buffer == NULL)
        return -1;

    while(fgets(buffer, MAX, stdin) != NULL)
    {
        buffer[strlen(buffer) - 1] = '\0';
        
        char *command = strtok(buffer, " ");

        if(strcmp(command, "mkdir") == 0)
        {
            char *dir_name = strtok(NULL, " ");
            mkdir(current, dir_name);
        }

        else if(strcmp(command, "touch") == 0)
        {
            char *file_name = strtok(NULL, " ");
            touch(current, file_name);
        }
        else if(strcmp(command, "ls") == 0)
        {
            ls_directory(current->directories);
            ls_files(current->files);
            printf("\n");

        }
        else if(strcmp(command, "quit") == 0)
        {   
            break;
        }
        else if(strcmp(command, "rm") == 0)
        {
            char *file_name = strtok(NULL, " ");

            int ok = 0;
            file *temp = current->files;
            while(temp != NULL)
            {   
                if(strcmp(temp->name, file_name) == 0)
                {
                    ok = 1;
                    break;
                }
                else if(strcmp(temp->name, file_name) > 0)
                    temp = temp->left;
                else
                    temp = temp->right;    
            }
            if(ok == 1)
                current->files = rm_file(current->files, file_name);
            else
                printf("File %s doesn't exist!\n", file_name);
            
        }
        else if(strcmp(command, "rmdir") == 0)
        {
            char *dir_name = strtok(NULL, " ");

            int ok = 0;
            directory *temp = current->directories;
            while(temp != NULL)
            {   
                if(strcmp(temp->name, dir_name) == 0)
                {
                    ok = 1;
                    break;
                }
                else if(strcmp(temp->name, dir_name) > 0)
                    temp = temp->left;
                else
                    temp = temp->right;    
            }
            if(ok == 1)
                current->directories = rm_directory(current->directories, dir_name);
            else
                printf("Directory %s doesn't exist!\n", dir_name);
        }
        else if(strcmp(command, "cd") == 0)
        {
            char *dir_name = strtok(NULL, " ");
            current = cd(current, dir_name);
            
        }
        else if(strcmp(command, "pwd") == 0)
        {
            pwd(current);
        }
        else if(strcmp(command, "find") == 0)
        {
            char *type = strtok(NULL, " ");
            char *name = strtok(NULL, " ");

            if(strcmp(type, "-d") == 0)
                find_directory(current, name);
            else if(strcmp(type, "-f") == 0)
                find_file(current, name);
        }
    }
destroy_directory(root->directories);
destroy_file(root->files);
destroy_directory(root);
free(buffer);

return 0;
}
