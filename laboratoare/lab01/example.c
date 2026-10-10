#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#define NUM_THREADS 2
// #define NUM_ITERATIONS 100

// void *f(void *arg) {
//   long id = *(long *)arg;
//   for (int i = 0; i < NUM_ITERATIONS; i++) {
//     printf("Hello World-ul %d din thread-ul %ld!\n", i + 1, id);
//   }
//   pthread_exit(NULL);
// }

void *f1(void *arg) {
  long id = *(long *)arg;
  printf("Hello World-ul din thread-ul %ld!\n", id);
  pthread_exit(NULL);
}

void *f2(void *arg) {
  long id = *(long *)arg;
  printf("Hello World-ul din thread-ul %ld!\n", id);
  pthread_exit(NULL);
}

int main(int argc, char *argv[]) {
  // long cores = sysconf(_SC_NPROCESSORS_CONF);
  // const long NUM_THREADS = cores;

  pthread_t threads[NUM_THREADS];
  int r;
  long id;
  void *status;
  long ids[NUM_THREADS];

  // for (id = 0; id < NUM_THREADS; id++) {
  //   ids[id] = id;
  //   r = pthread_create(&threads[id], NULL, f1, &ids[id]);

  //   if (r) {
  //     printf("Eroare la crearea thread-ului %ld\n", id);
  //     exit(-1);
  //   }
  // }

  ids[0] = 0;
  r = pthread_create(&threads[0], NULL, f1, &ids[0]);

  if (r) {
    printf("Eroare la crearea thread-ului %d\n", 0);
    exit(-1);
  }

  ids[1] = 1;
  r = pthread_create(&threads[1], NULL, f2, &ids[1]);

  if (r) {
    printf("Eroare la crearea thread-ului %d\n", 1);
    exit(-1);
  }

  for (id = 0; id < NUM_THREADS; id++) {
    r = pthread_join(threads[id], &status);

    if (r) {
      printf("Eroare la asteptarea thread-ului %ld\n", id);
      exit(-1);
    }
  }

  return 0;
}
