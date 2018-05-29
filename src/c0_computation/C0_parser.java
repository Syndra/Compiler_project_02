package c0_computation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class C0_parser {
	
	public List<String> raw_format_list;
	public List<Production_rule> rule_list;
	public List<Dot_rule> all_possible_dot_list;
	public Dot_rule Augmented_rules;
	public List<Iterator_c0> c0_i_list;
	public int rule_cnt;
	
	public C0_parser(String filePath) 
	{
		this.raw_format_list = new ArrayList<String>();
		this.rule_list = new ArrayList<Production_rule>();
		this.all_possible_dot_list = new ArrayList<Dot_rule>();
		
		this.c0_i_list = new ArrayList<Iterator_c0>();
		
		file_reader(filePath);
		format_distribute();
		compute_all_possible_dot_list();
		c0_compute();
	}
	
	public void file_reader(String filePath)
	{
		List<String> list = new ArrayList<String>();
		String tmp; // to read a line in txt file
		try {
			BufferedReader file = new BufferedReader(new FileReader(filePath));
			while ((tmp = file.readLine()) != null) {
				list.add(tmp);
			}
			file.close();
		} catch (IOException e) {
			System.err.println(e); // if it occur error, print.
			System.exit(1);
		}
		this.raw_format_list = list;
	}
	
	public void format_distribute() 
	{
		this.rule_cnt = raw_format_list.size() / 2;
		for(int i = 0; i < this.rule_cnt; i++) 
		{
			rule_list.add(new Production_rule(raw_format_list.get(i*2 + 1)));
		}
		rule_list.add(0, new Production_rule("S>" + rule_list.get(0).start));
		this.rule_cnt++;
	}
	
	public void compute_all_possible_dot_list() 
	{
		for(int i = 0; i < this.rule_cnt; i++) 
		{
			this.all_possible_dot_list.add(new Dot_rule(this.rule_list.get(i).start ,this.rule_list.get(i).rule.toString(), 0));
		}
	}
	
	public void c0_compute() {
		boolean changed = true;

		this.c0_i_list.add(new Iterator_c0(get_closure(this.all_possible_dot_list.get(0))));
		// this.c0_i_list.add(get_goto('E',
		// get_closure(this.all_possible_dot_list.get(0))));

		// this.print_string_in_dot_list(this.c0_i_list.get(0));
		// this.print_string_in_dot_list(this.c0_i_list.get(1));
		this.print_string_in_dot_list(this.c0_i_list.get(0).dot_rule_list);

		//this.c0_i_list.add(new Iterator_c0(get_goto('T', this.c0_i_list.get(0).dot_rule_list)));

		//this.print_string_in_dot_list(this.c0_i_list.get(1).dot_rule_list);

		//System.out.println(this.is_contain(this.c0_i_list, get_goto('T', this.c0_i_list.get(0).dot_rule_list)));

		// System.out.println("");
		//
		// this.c0_i_list.add(get_goto('+', this.c0_i_list.get(1)));
		// this.print_string_in_dot_list(this.c0_i_list.get(2));

		List<Character> char_list;
		List<Dot_rule> temp;
		List<Dot_rule> cur_dot_list;

		// for(int i = 0; i < this.c0_i_list.size(); i++)
		for (int i = 0; i < 30; i++) 
		{
			cur_dot_list = this.c0_i_list.get(i).dot_rule_list;
			char_list = this.get_char_next_dot_list(this.c0_i_list.get(i).dot_rule_list);
			
			System.out.println("---Compute goto I" + i + "  with" + char_list);
			this.print_string_in_dot_list(cur_dot_list);
		
			for (int j = 0; j < char_list.size(); j++) 
			{
				temp = this.get_goto(char_list.get(j), cur_dot_list);
				if (!this.is_contain(this.c0_i_list, temp))
				{

					System.out.println("GOTO (" + i + "," + this.get_char_next_dot_list(cur_dot_list).get(j) + ") = I " + (Iterator_c0.iterator_cnt));

					this.c0_i_list.add(new Iterator_c0(temp));

					this.print_string_in_dot_list(temp);
				}
				else;
			}
		}
		System.out.println(Iterator_c0.iterator_cnt);
	}
	
	public List<Dot_rule> get_closure(Dot_rule rule)
	{
		boolean changed = true;
		
		List<Character> char_list = new ArrayList<Character>();
		List<Dot_rule> list = new ArrayList<Dot_rule>();
		
		list.add(rule);
		if(rule.get_char_next_dot() == '>')
			return list;
		char_list.add(rule.get_char_next_dot());

		while(changed) {
			changed = false;
			for(Dot_rule elem : this.all_possible_dot_list) 
			{
				if(char_list.contains(elem.start)) 
				{
					if(!list.contains(elem)) 
					{
						if(!char_list.contains(elem.get_char_next_dot()))
							char_list.add(elem.get_char_next_dot());
						list.add(elem);
						changed = true;
					}
				}
			}
		}
		return list;
	}
	
	public List<Dot_rule> get_goto(char symbol, List<Dot_rule> rule_list)
	{
		List<Dot_rule> list = new ArrayList<Dot_rule>();
		for(Dot_rule elem : rule_list) 
		{
			if(elem.get_char_next_dot() == symbol) 
			{
				Dot_rule temp = new Dot_rule(elem.start, elem.rule.toString(), elem.dot_index);
				temp.dot_move_right();
				list = union(list, get_closure(temp));
			}
		}
		return list;
	}
	
	public List<Dot_rule> union(List<Dot_rule> target, List<Dot_rule> source)
	{
		for(Dot_rule elem : source) 
		{
			if(!target.contains(elem))
				target.add(elem);
		}
		return target;
	}
	
	public void print_string_in_dot_list(List<Dot_rule> list) 
	{
		for(Dot_rule elem : list) 
		{
			elem.print_dot_rule();
		}
	}
	
	public List<Character> get_char_next_dot_list(List<Dot_rule> rule_list) 
	{
		List<Character> char_list = new ArrayList<Character>();
		
		for(Dot_rule elem : rule_list) 
		{
			if(!char_list.contains(elem.get_char_next_dot())) 
			{
				if(elem.get_char_next_dot() != '>')
					char_list.add(elem.get_char_next_dot());
			}
		}
		
		return char_list;
	}
	
	public boolean is_contain(List<Iterator_c0> c0_list, List<Dot_rule> dot_rule) 
	{
		for(Iterator_c0 elem : c0_list) 
		{
			if(elem.is_same(dot_rule))
				return true;
		}
		return false;
	}
}
