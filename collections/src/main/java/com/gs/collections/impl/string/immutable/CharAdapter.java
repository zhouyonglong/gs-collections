/*
 * Copyright 2015 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gs.collections.impl.string.immutable;

import java.io.IOException;
import java.io.Serializable;
import java.util.NoSuchElementException;

import com.gs.collections.api.CharIterable;
import com.gs.collections.api.LazyCharIterable;
import com.gs.collections.api.bag.primitive.MutableCharBag;
import com.gs.collections.api.block.function.primitive.CharToCharFunction;
import com.gs.collections.api.block.function.primitive.CharToObjectFunction;
import com.gs.collections.api.block.function.primitive.ObjectCharIntToObjectFunction;
import com.gs.collections.api.block.function.primitive.ObjectCharToObjectFunction;
import com.gs.collections.api.block.predicate.primitive.CharPredicate;
import com.gs.collections.api.block.procedure.primitive.CharIntProcedure;
import com.gs.collections.api.block.procedure.primitive.CharProcedure;
import com.gs.collections.api.iterator.CharIterator;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.primitive.CharList;
import com.gs.collections.api.list.primitive.ImmutableCharList;
import com.gs.collections.api.list.primitive.MutableCharList;
import com.gs.collections.api.set.primitive.MutableCharSet;
import com.gs.collections.impl.bag.mutable.primitive.CharHashBag;
import com.gs.collections.impl.lazy.primitive.ReverseCharIterable;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.list.mutable.primitive.CharArrayList;
import com.gs.collections.impl.primitive.AbstractCharIterable;
import com.gs.collections.impl.set.mutable.primitive.CharHashSet;
import com.gs.collections.impl.utility.StringIterate;

/**
 * Provides a view into the char[] stored in a String as an ImmutableCharList.  This is a cleaner more OO way of
 * providing many of the iterable protocols available in StringIterate for char values.
 *
 * @since 7.0
 */
public class CharAdapter extends AbstractCharIterable implements CharSequence, ImmutableCharList, Serializable
{
    private static final long serialVersionUID = 1L;

    private final String adapted;

    public CharAdapter(String value)
    {
        this.adapted = value;
    }

    public static CharAdapter adapt(String value)
    {
        return new CharAdapter(value);
    }

    public static CharAdapter from(char... chars)
    {
        return new CharAdapter(new String(chars));
    }

    public static CharAdapter from(CharIterable iterable)
    {
        if (iterable instanceof CharAdapter)
        {
            return new CharAdapter(iterable.toString());
        }
        return new CharAdapter(iterable.makeString(""));
    }

    public char charAt(int index)
    {
        return this.adapted.charAt(index);
    }

    public int length()
    {
        return this.adapted.length();
    }

    public String subSequence(int start, int end)
    {
        return this.adapted.substring(start, end);
    }

    public StringBuilder toStringBuilder()
    {
        StringBuilder builder = new StringBuilder();
        int size = this.size();
        for (int i = 0; i < size; i++)
        {
            builder.append(this.get(i));
        }
        return builder;
    }

    /**
     * The value of toString must be strictly implemented as defined in CharSequence.
     */
    @Override
    public String toString()
    {
        return this.adapted;
    }

    public CharIterator charIterator()
    {
        return new InternalCharIterator();
    }

    public char[] toArray()
    {
        return this.adapted.toCharArray();
    }

    public boolean contains(final char expected)
    {
        return StringIterate.anySatisfyChar(this.adapted, new CharPredicate()
        {
            public boolean accept(char value)
            {
                return expected == value;
            }
        });
    }

    public void forEach(CharProcedure procedure)
    {
        this.each(procedure);
    }

    public void each(CharProcedure procedure)
    {
        StringIterate.forEachChar(this.adapted, procedure);
    }

    public CharAdapter distinct()
    {
        StringBuilder builder = new StringBuilder();
        CharHashSet seenSoFar = new CharHashSet();

        int size = this.size();
        for (int i = 0; i < size; i++)
        {
            char each = this.get(i);
            if (seenSoFar.add(each))
            {
                builder.append(each);
            }
        }
        return new CharAdapter(builder.toString());
    }

    public CharAdapter newWith(char element)
    {
        return new CharAdapter(this.adapted + element);
    }

    public CharAdapter newWithout(char element)
    {
        StringBuilder builder = new StringBuilder(this.adapted);
        int indexToRemove = this.indexOf(element);
        if (indexToRemove < 0)
        {
            return this;
        }
        builder.deleteCharAt(indexToRemove);
        return new CharAdapter(builder.toString());
    }

    public CharAdapter newWithAll(CharIterable elements)
    {
        MutableCharList mutableCharList = this.toList();
        mutableCharList.addAll(elements);
        return new CharAdapter(new String(mutableCharList.toArray()));
    }

    public CharAdapter newWithoutAll(CharIterable elements)
    {
        MutableCharList mutableCharList = this.toList();
        mutableCharList.removeAll(elements);
        return new CharAdapter(new String(mutableCharList.toArray()));
    }

    public CharAdapter toReversed()
    {
        StringBuilder builder = new StringBuilder(this.adapted);
        return new CharAdapter(builder.reverse().toString());
    }

    public ImmutableCharList subList(int fromIndex, int toIndex)
    {
        throw new UnsupportedOperationException("SubList is not implemented on CharAdapter");
    }

    public char get(int index)
    {
        return this.adapted.charAt(index);
    }

    public Character getCharacter(int index)
    {
        return Character.valueOf(this.get(index));
    }

    public long dotProduct(CharList list)
    {
        throw new UnsupportedOperationException("DotProduct is not implemented on CharAdapter");
    }

    public int binarySearch(char value)
    {
        throw new UnsupportedOperationException("BinarySearch is not implemented on CharAdapter");
    }

    public int lastIndexOf(char value)
    {
        for (int i = this.size() - 1; i >= 0; i--)
        {
            if (this.get(i) == value)
            {
                return i;
            }
        }
        return -1;
    }

    public ImmutableCharList toImmutable()
    {
        return this;
    }

    public char getLast()
    {
        return this.get(this.size() - 1);
    }

    public LazyCharIterable asReversed()
    {
        return ReverseCharIterable.adapt(this);
    }

    public <T> T injectIntoWithIndex(T injectedValue, ObjectCharIntToObjectFunction<? super T, ? extends T> function)
    {
        T result = injectedValue;
        int size = this.size();
        for (int i = 0; i < size; i++)
        {
            result = function.valueOf(result, this.get(i), i);
        }
        return result;
    }

    public char getFirst()
    {
        return this.get(0);
    }

    public int indexOf(char value)
    {
        int size = this.size();
        for (int i = 0; i < size; i++)
        {
            if (this.get(i) == value)
            {
                return i;
            }
        }
        return -1;
    }

    public void forEachWithIndex(CharIntProcedure procedure)
    {
        int size = this.size();
        for (int i = 0; i < size; i++)
        {
            procedure.value(this.get(i), i);
        }
    }

    public CharAdapter select(CharPredicate predicate)
    {
        return new CharAdapter(StringIterate.selectChar(this.adapted, predicate));
    }

    public CharAdapter reject(CharPredicate predicate)
    {
        return new CharAdapter(StringIterate.rejectChar(this.adapted, predicate));
    }

    public <V> ImmutableList<V> collect(CharToObjectFunction<? extends V> function)
    {
        int size = this.size();
        FastList<V> list = FastList.newList(size);
        for (int i = 0; i < size; i++)
        {
            list.add(function.valueOf(this.get(i)));
        }
        return list.toImmutable();
    }

    public CharAdapter collectChar(CharToCharFunction function)
    {
        StringBuilder builder = new StringBuilder(this.length());
        int size = this.size();
        for (int i = 0; i < size; i++)
        {
            builder.append(function.valueOf(this.get(i)));
        }
        return new CharAdapter(builder.toString());
    }

    public char detectIfNone(CharPredicate predicate, char ifNone)
    {
        return StringIterate.detectCharIfNone(this.adapted, predicate, ifNone);
    }

    public int count(CharPredicate predicate)
    {
        return StringIterate.countChar(this.adapted, predicate);
    }

    public boolean anySatisfy(CharPredicate predicate)
    {
        return StringIterate.anySatisfyChar(this.adapted, predicate);
    }

    public boolean allSatisfy(CharPredicate predicate)
    {
        return StringIterate.allSatisfyChar(this.adapted, predicate);
    }

    public boolean noneSatisfy(CharPredicate predicate)
    {
        return StringIterate.noneSatisfyChar(this.adapted, predicate);
    }

    @Override
    public MutableCharList toList()
    {
        int size = this.size();
        CharArrayList list = new CharArrayList(size);
        for (int i = 0; i < size; i++)
        {
            list.add(this.get(i));
        }
        return list;
    }

    @Override
    public MutableCharSet toSet()
    {
        int size = this.size();
        CharHashSet set = new CharHashSet(size);
        for (int i = 0; i < size; i++)
        {
            set.add(this.get(i));
        }
        return set;
    }

    @Override
    public MutableCharBag toBag()
    {
        int size = this.size();
        CharHashBag bag = new CharHashBag(size);
        for (int i = 0; i < size; i++)
        {
            bag.add(this.get(i));
        }
        return bag;
    }

    public <T> T injectInto(T injectedValue, ObjectCharToObjectFunction<? super T, ? extends T> function)
    {
        T result = injectedValue;
        int size = this.size();
        for (int i = 0; i < size; i++)
        {
            result = function.valueOf(result, this.get(i));
        }
        return result;
    }

    public long sum()
    {
        long sum = 0;
        int size = this.size();
        for (int i = 0; i < size; i++)
        {
            sum += this.get(i);
        }
        return sum;
    }

    public char max()
    {
        if (this.isEmpty())
        {
            throw new NoSuchElementException();
        }
        char max = this.get(0);
        int size = this.size();
        for (int i = 1; i < size; i++)
        {
            char value = this.get(i);
            if (max < value)
            {
                max = value;
            }
        }
        return max;
    }

    public char min()
    {
        if (this.isEmpty())
        {
            throw new NoSuchElementException();
        }
        char min = this.get(0);
        int size = this.size();
        for (int i = 1; i < size; i++)
        {
            char value = this.get(i);
            if (value < min)
            {
                min = value;
            }
        }
        return min;
    }

    public int size()
    {
        return this.adapted.length();
    }

    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        try
        {
            appendable.append(start);
            int size = this.size();
            for (int i = 0; i < size; i++)
            {
                if (i > 0)
                {
                    appendable.append(separator);
                }
                char value = this.get(i);
                appendable.append(value);
            }
            appendable.append(end);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object otherList)
    {
        if (otherList == this)
        {
            return true;
        }
        if (!(otherList instanceof CharList))
        {
            return false;
        }
        CharList list = (CharList) otherList;
        if (this.size() != list.size())
        {
            return false;
        }
        for (int i = 0; i < this.size(); i++)
        {
            if (this.get(i) != list.get(i))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hashCode = 1;
        for (int i = 0; i < this.size(); i++)
        {
            char item = this.get(i);
            hashCode = 31 * hashCode + (int) item;
        }
        return hashCode;
    }

    private class InternalCharIterator implements CharIterator
    {
        /**
         * Index of element to be returned by subsequent call to next.
         */
        private int currentIndex;

        public boolean hasNext()
        {
            return this.currentIndex != CharAdapter.this.adapted.length();
        }

        public char next()
        {
            if (!this.hasNext())
            {
                throw new NoSuchElementException();
            }
            char next = CharAdapter.this.adapted.charAt(this.currentIndex);
            this.currentIndex++;
            return next;
        }
    }
}
