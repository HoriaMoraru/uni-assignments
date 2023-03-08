#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <inttypes.h>
#include "structs.h"

#define MAX 256;

int add_last(void **arr, int *len, data_structure *data)
{	
	int length = 0;
	if (*len == 0) 
	{	
		*arr = malloc(sizeof(head) + data->header->len);
		memcpy(*arr, data->header, sizeof(head));
		memcpy(*arr + sizeof(head), data->data, data->header->len);
		(*len) += 1;
	}
	else
	{	
		for (int i = 0; i < *len; i++)
		{
			unsigned int data_len = *(unsigned int*)(*arr + sizeof(unsigned char) + 3 + length + sizeof(head) * i);
			length += data_len;
		}
		*arr = realloc(*arr, length + (*len + 1) * sizeof(head) + data->header->len);
		memcpy(*arr + (*len) * sizeof(head) + length, data->header, sizeof(head));
		memcpy(*arr + (*len+1) * sizeof(head) + length, data->data, data->header->len);
		(*len) += 1;
	
	}
	
	return 0;
}

int add_at(void **arr, int *len, data_structure *data, int index)
{	
	int length_before = 0;

	if(index > *len)
	{	
		add_last(arr, len, data);
		return 0;
	}

	for(int i = 0; i < index; i++)
	{
		unsigned int data_len = *(unsigned int*)(*arr + sizeof(unsigned char) + 3 + length_before + sizeof(head) * i);
			length_before += data_len;
	}																																								
	int length_current = *(unsigned int*)(*arr + sizeof(head) * index + length_before + sizeof(unsigned char) + 3);
	int length_after = 0;
	int tmp = length_before + length_current + sizeof(head) * (index + 1);
	int nr = *len - index - 1;

	for(int i = 0; i < nr; i++)
	{
		unsigned int data_len = *(unsigned int*)(*arr + tmp + sizeof(unsigned char) + 3 + length_after + sizeof(head) * i);
			length_after += data_len;
	}

	*arr = realloc(*arr, length_before + length_after + length_current + (*len + 1) * sizeof(head) + data->header->len);
	unsigned char * aux = malloc(length_after + length_current + sizeof(head) * (nr + 1));
	memcpy(aux, *arr + length_before + sizeof(head) * index, length_after + length_current + sizeof(head) * (nr + 1));

	memcpy(*arr + length_before + sizeof(head) * index, data->header, sizeof(head));
	memcpy(*arr + length_before + sizeof(head) * index + sizeof(head), data->data, data->header->len);
	memcpy(*arr + length_before + sizeof(head) * index + sizeof(head) + data->header->len,
			aux,
			length_after + length_current + sizeof(head) * (nr + 1));

	*len += 1;
	free(aux);
	return 0;
}

void find(void *data_block, int len, int index) 
{
	void * a = data_block;

	if(index < 0 || index > len)
		printf("Eroare");

	else
	{	
		unsigned char type;
		int offset = 0, offset1 = 0, offset2 = 0;
		for(int i = 0; i < index + 1; i++)
		{	
			type = *(unsigned char*)(a + offset);
			offset += sizeof(head);

			offset1 = offset;

			offset += strlen(a + offset) + 1;
			offset2 = offset;

			if(type == '1')
				offset += 2 * sizeof(int8_t) + strlen(a + offset + 2 * sizeof(int8_t)) + 1;

			else if(type == '2')
				offset += sizeof(int16_t) + sizeof(int32_t) + strlen(a + offset + sizeof(int16_t) + sizeof(int32_t)) + 1;

			else if(type == '3')
				offset += 2 * sizeof(int32_t) + strlen(a + offset + 2 * sizeof(int32_t)) + 1;
		}
			printf("Tipul %c\n", type);
			printf("%s ",(char* )a + offset1);
			printf("pentru");

			if(type == '1')
			{
				printf(" %s\n", (char *)a + offset2 + 2 * sizeof(int8_t));
				printf("%"PRId8"\n", *(int8_t*)(a + offset2));
				printf("%"PRId8"\n", *(int8_t*)(a + offset2 + sizeof(int8_t)));
			}
			else if(type == '2')
			{
				printf(" %s\n",(char *)a + offset2 + sizeof(int16_t) + sizeof(int32_t));
				printf("%"PRId16"\n", *(int16_t*)(a + offset2));
				printf("%"PRId32"\n", *(int32_t*)(a + offset2 + sizeof(int16_t)));
			}
			else if(type == '3')
			{
				printf(" %s\n", (char *)a + offset2 + sizeof(int32_t) + sizeof(int32_t));
				printf("%"PRId32"\n", *(int32_t*)(a + offset2));
				printf("%"PRId32"\n", *(int32_t*)(a + offset2 + sizeof(int32_t)));
			}
			printf("\n");
	}
}

int delete_at(void **arr, int *len, int index)
{	
	int length_before = 0;

	for(int i = 0; i < index; i++)
	{
		unsigned int data_len = *(unsigned int*)(*arr + sizeof(unsigned char) + 3 + length_before + sizeof(head) * i);
			length_before += data_len;
	}																																								

	int length_current = *(unsigned int*)(*arr + sizeof(head) * index + length_before + sizeof(unsigned char) + 3);

	int length_after = 0;
	int tmp = length_before + length_current + sizeof(head) * (index + 1);
	int nr = *len - index - 1;

	for(int i = 0; i < nr; i++)
	{
		unsigned int data_len = *(unsigned int*)(*arr + tmp + sizeof(unsigned char) + 3 + length_after + sizeof(head) * i);
			length_after += data_len;
	}	

	memcpy(*arr + length_before + sizeof(head) * index, 
		   *arr + length_before + sizeof(head) * index + sizeof(head) + length_current,
		   sizeof(head) * nr + length_after);

	*len -= 1;
	*arr = realloc(*arr, length_before + sizeof(head) * index + length_after + sizeof(head) * nr);

	return 0;
}

void print(void *arr, int len)
{	
	void* a = arr;
	int offset = 0;
	for(int i = 0; i < len; i++)
	{	
		unsigned char type = *(unsigned char*)(a + offset);
		offset += sizeof(head);

		printf("Tipul %c\n", type);

		printf("%s ",(char* )a + offset);

		offset += strlen(a + offset) + 1;

		printf("pentru");

		if(type == '1')
		{	
			printf(" %s\n", (char *)a + offset + 2 * sizeof(int8_t));
			printf("%"PRId8"\n", *(int8_t*)(a + offset));
			printf("%"PRId8"\n", *(int8_t*)(a + offset + sizeof(int8_t)));

			offset += 2 * sizeof(int8_t) + strlen(a + offset + 2 * sizeof(int8_t)) + 1;
		}

		else if(type == '2')
		{
			printf(" %s\n",(char *)a + offset + sizeof(int16_t) + sizeof(int32_t));
			printf("%"PRId16"\n", *(int16_t*)(a + offset));
			printf("%"PRId32"\n", *(int32_t*)(a + offset + sizeof(int16_t)));

			offset += sizeof(int16_t) + sizeof(int32_t) + strlen(a + offset + sizeof(int16_t) + sizeof(int32_t)) + 1;
		}

		else if(type == '3')
		{
			printf(" %s\n", (char *)a + offset + sizeof(int32_t) + sizeof(int32_t));
			printf("%"PRId16"\n", *(int32_t*)(a + offset));
			printf("%"PRId32"\n", *(int32_t*)(a + offset + sizeof(int32_t)));

			offset += 2 * sizeof(int32_t) + strlen(a + offset + 2 * sizeof(int32_t)) + 1;
		}
	printf("\n");
	}
}

int main() 
{
	void *arr = NULL;
	int len = 0;

	char * line = malloc(256 * (sizeof(char)));
	
	if(line == NULL)
		exit(1);
	
	while(fgets(line, 256, stdin) != NULL)
	{	
		char * command = strtok(line, " ");

		if(strcmp(command, "exit\n") == 0)
		{	
			free(line);
			free(arr);
			break;
		}
		if(strcmp(command, "insert") == 0 || strcmp(command, "insert_at") == 0)
		{	
			data_structure * data = malloc(sizeof(data_structure));
			data->header = malloc(sizeof(head));
			data->header->len = 0;

			int index = 0;

			if(strcmp(command, "insert_at") == 0)
				index = atoi(strtok(NULL, " "));

			int8_t * b1, * b2;
			int16_t * b3;
			int32_t * b4, * b5, * b6;

			char * token = strtok(NULL, " ");
			data->header->type = token[0];

			char * first;
			char * second;

			token = strtok(NULL, " ");
			first = malloc(strlen(token) + 1);
			strcpy(first, token);
			data->header->len += strlen(first) + 1;
			token = strtok(NULL, " ");

			if(data->header->type == '1')
			{
				b1 = malloc(sizeof(int8_t));
				b2 = malloc(sizeof(int8_t));

				*b1 = atoi(token);
				*b2 = atoi(strtok(NULL, " "));

				data->header->len += sizeof(int8_t) * 2;
			}
			else if (data->header->type == '2')
			{
				b3 = malloc(sizeof(int16_t));
				b4 = malloc(sizeof(int32_t));
				*b3 = atoi(token);
				*b4 = atoi(strtok(NULL, " "));

				data->header->len += sizeof(int16_t) + sizeof(int32_t);
			} 
			else if (data->header->type == '3')
			{

				b5 = malloc(sizeof(int32_t));
				b6 = malloc(sizeof(int32_t));

				*b5 = atoi(token);
				*b6 = atoi(strtok(NULL, " "));

				data->header->len += sizeof(int32_t) * 2; 
			}

			token = strtok(NULL, " ");
			
			second = malloc(strlen(token) + 1);
			strcpy(second , token);

			second[strlen(second) - 1] = '\0';  // setez ca ultimul caracter sa fie terminatorul de sir 

			data->header->len += strlen(second) +1;

			data->data = malloc(data->header->len);
			memcpy(data->data, first, strlen(first) + 1);
			if(data->header->type == '1')
			{
				memcpy(data->data + strlen(first) + 1, b1, sizeof(int8_t));
				memcpy(data->data + strlen(first) + sizeof(int8_t) + 1, b2, sizeof(int8_t));
				memcpy(data->data + strlen(first) + sizeof(int8_t) + sizeof(int8_t) +1, second, strlen(second) + 1);
				free(first);
				free(second);
				free(b1);
				free(b2);
			}

			else if(data->header->type == '2')
			{
				memcpy(data->data + strlen(first) + 1, b3, sizeof(int16_t));
				memcpy(data->data + strlen(first) + sizeof(int16_t) + 1, b4, sizeof(int32_t));
				memcpy(data->data + strlen(first) + sizeof(int16_t) + sizeof(int32_t) +1, second, strlen(second) + 1);
				free(first);
				free(second);
				free(b3);
				free(b4);
			}
			else if(data->header->type == '3')
			{
				memcpy(data->data + strlen(first) + 1, b5, sizeof(int32_t));
				memcpy(data->data + strlen(first) + sizeof(int32_t) + 1, b6, sizeof(int32_t));
				memcpy(data->data + strlen(first) + sizeof(int32_t) + sizeof(int32_t) +1, second, strlen(second) + 1);
				free(first);
				free(second);
				free(b5);
				free(b6);
			}

			if(strcmp(command, "insert") == 0)
				add_last(&arr, &len, data);
			else 
				add_at(&arr, &len, data, index);
		
		free(data->header);
		free(data->data);
		free(data);

		}

		if(strcmp(command, "print\n") == 0)
			print(arr, len);

		if(strcmp(command, "delete_at") == 0)
		{
			int index = 0;

			index = atoi(strtok(NULL, " "));
			delete_at(&arr, &len, index);

		}

		if(strcmp(command, "find") == 0)
		{
			int index = 0;

			index = atoi(strtok(NULL, " "));
			find(arr, len, index);

		}

	}
	return 0;
}