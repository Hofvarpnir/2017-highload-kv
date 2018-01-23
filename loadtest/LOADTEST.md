# Нагрузочное тестирование
Тестирование происходит на виртуальной машине Ubuntu 16.04 с SSD. Хранение информации в проекте реализовано на обычных файлах.

## Тестирование

### PUT без перезаписи
Производится PUT запрос, записывая по очереди на разные id. 

```
esbor@Dienheim$ wrk --latency -c4 -d1m -s loadtest/PUTnoR.lua http://localhost:8080/
Running 1m test @ http://localhost:8080/
  2 threads and 4 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   492.05us    0.90ms  27.31ms   93.78%
    Req/Sec     4.05k     0.90k    5.29k    66.00%
  Latency Distribution
     50%  283.00us
     75%  347.00us
     90%  689.00us
     99%    4.74ms
  483596 requests in 1.00m, 55.80MB read
Requests/sec:   8046.58
Transfer/sec:      0.93MB

```

### PUT с перезаписью

Производится PUT запрос, записывая по очереди всего на 4 id. 

```
esbor@Dienheim$ wrk --latency -c4 -d1m -s loadtest/PUTR.lua http://localhost:8080/
Running 1m test @ http://localhost:8080/
  2 threads and 4 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   529.58us    0.98ms  22.62ms   93.15%
    Req/Sec     3.92k     0.94k    5.24k    60.68%
  Latency Distribution
     50%  282.00us
     75%  353.00us
     90%  849.00us
     99%    5.10ms
  467697 requests in 1.00m, 53.97MB read
Requests/sec:   7792.67
Transfer/sec:      0.90MB

```
PUT с перезаписью выполняется медленнее (в случае 99% значительно медленнее), чем PUT без перезаписи.

### GET без повторов
Перед тестированием GET запускаем скрипт data_GETnoR.lua для создания записей.

```
esbor@Dienheim$ wrk --latency -c4 -d1m -s loadtest/GETnoR.lua http://localhost:8080/
Running 1m test @ http://localhost:8080/
  2 threads and 4 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   493.82us    1.04ms  36.22ms   94.07%
    Req/Sec     4.12k     0.91k    5.27k    74.48%
  Latency Distribution
     50%  276.00us
     75%  339.00us
     90%  673.00us
     99%    4.56ms
  492311 requests in 1.00m, 56.81MB read
Requests/sec:   8192.50
Transfer/sec:      0.95MB

```

### GET с повторами

Производится GET запрос, считывая с первых 30 id

```
esbor@Dienheim$ wrk --latency -c4 -d1m -s loadtest/GETR.lua http://localhost:8080/
Running 1m test @ http://localhost:8080/
  2 threads and 4 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   506.69us    0.96ms  24.25ms   93.45%
    Req/Sec     4.05k     0.97k    5.29k    68.92%
  Latency Distribution
     50%  276.00us
     75%  340.00us
     90%  733.00us
     99%    4.96ms
  483943 requests in 1.00m, 55.84MB read
Requests/sec:   8055.38
Transfer/sec:      0.93MB

```
Аналогично PUT с перезаписью, GET с повторами выполняется немного медленнее (в случае 99% значительно медленнее).