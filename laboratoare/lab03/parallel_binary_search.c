#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <pthread.h>

#include "pthread_barrier_mac.h"

#define INSIDE -1 // daca numarul cautat este in interiorul intervalului
#define OUTSIDE -2 // daca numarul cautat este in afara intervalului

struct my_arg {
	int id;
	int N;
	int P;
	int number;
	int *left;
	int *right;
	int *keep_running;
	int *v;
	int *found;
};

pthread_barrier_t barrier;

/*
void binary_search() {
	while (keep_running) {
		int mid = left + (right - left) / 2;
		if (left > right) {
			printf("Number not found\n");
			break;
		}

		if (v[mid] == number) {
			keep_running = 0;
			printf("Number found at position %d\n", mid);
		} else if (v[mid] > number) {
			left = mid + 1;
		} else {
			right = mid - 1;
		}
	}
}
*/

void *f(void *arg)
{
	struct my_arg *d = (struct my_arg *) arg;
	int id = d->id, P = d->P;
	int *found = d->found;

	while (*d->keep_running && *d->left < *d->right) {
		int left = *d->left;
		int size = *d->right - left;
		int p = left + (long long)(id + 1) * size / (P + 1);

		if (d->v[p] < d->number)      found[id] = -1;
		else if (d->v[p] > d->number) found[id] = 1;
		else                          found[id] = 0;

		pthread_barrier_wait(&barrier);   // all probes done

		if (found[id] == 0) {
			if (id == 0 || found[id - 1] != 0) {
				printf("Number found at position %d\n", p);
				*d->keep_running = 0;
			}
		} else if (found[id] < 0) {
			if (id == P - 1 || found[id + 1] > 0)
				*d->left = p + 1;
		} else {
			if (id == 0 || found[id - 1] < 0)
				*d->right = p;
		}

		pthread_barrier_wait(&barrier);   // updates done before the next check
	}

	return NULL;
}

void display_vector(int *v, int size) {
	int i;

	for (i = 0; i < size; i++) {
		printf("%d ", v[i]);
	}

	printf("\n");
}


int main(int argc, char *argv[])
{
	int r, N, P = 0, number, keep_running, left, right;
	int *v;
	int *found;
	void *status;
	pthread_t *threads;
	struct my_arg *arguments;

	pthread_barrier_init(&barrier, NULL, P);

	if (argc < 4) {
		printf("Usage:\n\t./ex N P number\n");
		return 1;
	}

	N = atoi(argv[1]);
	P = atoi(argv[2]);
	number = atoi(argv[3]);

	keep_running = 1;
	left = 0;
	right = N;

	v = (int*) malloc(N * sizeof(int));
	threads = (pthread_t*) malloc(P * sizeof(pthread_t));
	arguments = (struct my_arg*) malloc(P * sizeof(struct my_arg));
	found = (int*) malloc(P * sizeof(int));

	for (int i = 0; i < N; i++) {
		v[i] = i * 2;
	}

	display_vector(v, N);

	for (int i = 0; i < P; i++) {
		arguments[i].id = i;
		arguments[i].N = N;
		arguments[i].P = P;
		arguments[i].number = number;
		arguments[i].left = &left;
		arguments[i].right = &right;
		arguments[i].keep_running = &keep_running;
		arguments[i].v = v;
		arguments[i].found = found;

		r = pthread_create(&threads[i], NULL, f, &arguments[i]);

		if (r) {
			printf("Eroare la crearea thread-ului %d\n", i);
			exit(-1);
		}
	}

	for (int i = 0; i < P; i++) {
		r = pthread_join(threads[i], &status);

		if (r) {
			printf("Eroare la asteptarea thread-ului %d\n", i);
			exit(-1);
		}
	}

	pthread_barrier_destroy(&barrier);

	if (keep_running) {    // nobody found it
		printf("Number not found\n");
	}

	free(found);  

	free(v);
	free(threads);
	free(arguments);

	return 0;
}
