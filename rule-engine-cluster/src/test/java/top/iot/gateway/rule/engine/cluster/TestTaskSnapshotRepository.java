package top.iot.gateway.rule.engine.cluster;

import top.iot.gateway.rule.engine.api.task.TaskSnapshot;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class TestTaskSnapshotRepository implements TaskSnapshotRepository {
    private final Set<TaskSnapshot> all = new ConcurrentSkipListSet<>(Comparator.comparing(TaskSnapshot::getId));

    @Override
    public Flux<TaskSnapshot> findAllTask() {
        return Flux.fromIterable(all);
    }

    @Override
    public Flux<TaskSnapshot> findByInstanceId(String instanceId) {
        return findAllTask().filter(s -> s.getInstanceId().equals(instanceId));
    }

    @Override
    public Flux<TaskSnapshot> findByWorkerId(String workerId) {
        return findAllTask().filter(s -> s.getWorkerId().equals(workerId));
    }

    @Override
    public Flux<TaskSnapshot> findBySchedulerId(String schedulerId) {
        return findAllTask().filter(s -> s.getSchedulerId().equals(schedulerId));
    }

    @Override
    public Flux<TaskSnapshot> findBySchedulerIdNotIn(Collection<String> schedulerId) {
        return findAllTask().filter(s -> !schedulerId.contains(s.getSchedulerId()));
    }

    @Override
    public Flux<TaskSnapshot> findByInstanceIdAndWorkerId(String instanceId, String workerId) {
        return findByInstanceId(instanceId)
                .filter(s -> s.getWorkerId().equals(workerId))
                ;
    }

    @Override
    public Flux<TaskSnapshot> findByInstanceIdAndNodeId(String instanceId, String nodeId) {
        return findByInstanceId(instanceId)
                .filter(s -> s.getJob().getNodeId().equals(nodeId));
    }

    @Override
    public Mono<Void> saveTaskSnapshots(Publisher<TaskSnapshot> snapshots) {
        return Flux.from(snapshots)
                   .collectList()
                   .doOnNext(all::addAll)
                   .then();
    }

    @Override
    public Mono<Void> removeTaskByInstanceId(String instanceId) {
        return findByInstanceId(instanceId)
                .doOnNext(all::remove)
                .then();
    }

    @Override
    public Mono<Void> removeTaskByInstanceIdAndNodeId(String instanceId, String nodeId) {
        return findByInstanceIdAndNodeId(instanceId, nodeId)
                .doOnNext(all::remove)
                .then();
    }

    @Override
    public Mono<Void> removeTaskById(String id) {
        TaskSnapshot snapshot = new TaskSnapshot();
        snapshot.setId(id);
        all.remove(snapshot);
        return Mono.empty();
    }
}
