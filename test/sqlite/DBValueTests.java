package sqlite;

public class DBValueTests extends DBConnectionFixture {
  public void testString() throws SQLiteException {
    SQLiteConnection con = fileDb().open();
    SQLiteStatement st;

    st = insertAndSelect(con, "xyz");
    assertEquals("xyz", st.columnString(0));
    assertFalse(st.columnNull(0));
    st.clear();

    st = insertAndSelect(con, "1");
    assertEquals("1", st.columnString(0));
    assertEquals(1, st.columnInt(0));
    assertEquals(1, st.columnLong(0));
    assertFalse(st.columnNull(0));
    st.clear();

    st = insertAndSelect(con, "");
    assertEquals("", st.columnString(0));
    assertFalse(st.columnNull(0));
    st.clear();

    st = insertAndSelect(con, null);
    assertNull(st.columnString(0));
    assertTrue(st.columnNull(0));
    st.clear();

    st = insertNullAndSelect(con);
    assertNull(st.columnString(0));
    assertTrue(st.columnNull(0));
    st.clear();
  }

  public void testIntegerAndLong() throws SQLiteException {
    SQLiteConnection con = fileDb().open();
    SQLiteStatement st;

    st = insertAndSelect(con, 1, false);
    assertEquals(1, st.columnInt(0));
    assertEquals(1L, st.columnLong(0));
    assertEquals("1", st.columnString(0));
    assertFalse(st.columnNull(0));
    st.clear();

    st = insertAndSelect(con, 1, true);
    assertEquals(1, st.columnInt(0));
    assertEquals(1L, st.columnLong(0));
    assertEquals("1", st.columnString(0));
    assertFalse(st.columnNull(0));
    st.clear();

    st = insertAndSelect(con, Integer.MIN_VALUE, false);
    assertEquals(Integer.MIN_VALUE, st.columnInt(0));
    assertEquals((long) Integer.MIN_VALUE, st.columnLong(0));
    assertFalse(st.columnNull(0));
    st.clear();

    st = insertAndSelect(con, Integer.MAX_VALUE, false);
    assertEquals(Integer.MAX_VALUE, st.columnInt(0));
    assertEquals((long) Integer.MAX_VALUE, st.columnLong(0));
    assertFalse(st.columnNull(0));
    st.clear();

    long v = Integer.MAX_VALUE;
    v += 2;
    st = insertAndSelect(con, v, true);
    assertEquals(Integer.MIN_VALUE + 1, st.columnInt(0));
    assertEquals(v, st.columnLong(0));
    assertFalse(st.columnNull(0));
    st.clear();

    st = insertNullAndSelect(con);
    assertEquals(0, st.columnInt(0));
    assertEquals(0, st.columnLong(0));
    assertTrue(st.columnNull(0));
    st.clear();

    st = insertAndSelect(con, Integer.MAX_VALUE, false);
    st.clear();
    con.exec("update x set x = x + 2");
    st = con.prepare("select x from x");
    st.step();
    assertEquals(Integer.MIN_VALUE + 1, st.columnInt(0));
    assertEquals(((long) Integer.MAX_VALUE) + 2L, st.columnLong(0));
    assertFalse(st.columnNull(0));
    st.clear();

    st = insertAndSelect(con, Long.MAX_VALUE, true);
    st.clear();
    con.exec("update x set x = x + 2");
    st = con.prepare("select x from x");
    st.step();
    assertEquals(Long.MIN_VALUE + 1L, st.columnLong(0));
    assertFalse(st.columnNull(0));
    st.clear();
  }

  public void testFloats() throws SQLiteException {
    SQLiteConnection con = fileDb().open();
    SQLiteStatement st;

    double v = 1.1;
    st = insertAndSelect(con, v);
    assertEquals(v, st.columnDouble(0));
    assertFalse(st.columnNull(0));
    st.clear();
  }

  private static SQLiteStatement insertNullAndSelect(SQLiteConnection con) throws SQLiteException {
    recreateX(con);
    SQLiteStatement st = con.prepare("insert into x values (?)");
    st.bindNull(1);
    st.step();
    st.clear();
    st = con.prepare("select x from x");
    st.step();
    assertTrue(st.hasRow());
    return st;
  }

  private static SQLiteStatement insertAndSelect(SQLiteConnection con, double value) throws SQLiteException {
    recreateX(con);
    SQLiteStatement st = con.prepare("insert into x values (?)");
    st.bind(1, value);
    st.step();
    st.clear();
    st = con.prepare("select x from x");
    st.step();
    assertTrue(st.hasRow());
    return st;
  }

  private static SQLiteStatement insertAndSelect(SQLiteConnection con, String value) throws SQLiteException {
    recreateX(con);
    SQLiteStatement st = con.prepare("insert into x values (?)");
    st.bind(1, value);
    st.step();
    st.clear();
    st = con.prepare("select x from x");
    st.step();
    assertTrue(st.hasRow());
    return st;
  }

  private static SQLiteStatement insertAndSelect(SQLiteConnection con, long value, boolean useLong) throws SQLiteException {
    recreateX(con);
    SQLiteStatement st = con.prepare("insert into x values (?)");
    if (useLong) {
      st.bind(1, value);
    } else {
      st.bind(1, (int) value);
    }
    st.step();
    st.clear();
    st = con.prepare("select x from x");
    st.step();
    assertTrue(st.hasRow());
    return st;
  }

  private static void recreateX(SQLiteConnection con) throws SQLiteException {
    con.exec("drop table if exists x");
    con.exec("create table x (x)");
  }
}
