package db.transaction;

import db.exception.TransactionNotBeginException;

import java.util.ArrayList;
import java.util.List;

// MemoryDb事务实现，事务接口的调用顺序为begin -> exec -> exec > ... -> commit
public class Transaction {
    private final String name;
    private List<Command<?,?>> cmds;

    private Transaction(String name) {
        this.name = name;
    }

    public static Transaction of(String name) {
        return new Transaction(name);
    }

    // 开启一个事务
    public void begin() {
        this.cmds = new ArrayList<>();
    }

    // 在事务中执行命令，先缓存到cmds队列中，等commit时一把执行
    public void exec(Command<?,?> command) {
        if (this.cmds == null) {
            throw new TransactionNotBeginException(name);
        }
        cmds.add(command);
    }

    // 提交事务，执行队列中的命令，如果有命令失败，则回滚后再抛出异常
    public void commit() {
        ExecHistory history = ExecHistory.create();
        try {
            for (Command<?,?> cmd : cmds) {
                cmd.exec();
                history.add(cmd);
            }
        } catch (RuntimeException e) {
            history.rollback();
            throw e;
        }
    }
}
